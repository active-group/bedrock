(ns bedrock.spa
  (:require [bedrock.core :as b]
            #?(:clj [bedrock.ring :as ring])
            #?(:cljs [bedrock.csr :as csr])
            [active.clojure.functions :as f]
            [reacl-c-basics.pages.routes :as routes]))

(defn page [app route item-f]
  (let [route (if (string? route) (routes/route route) route)]
    (-> app
        (b/update-setting ::spa-routes conj route)
        #?(:cljs (csr/serve route item-f)))))

#?(:clj
   (do
     (defn- matches [route req]
       (routes/route-matches route req))
    
     (defn- matches-any [routes req]
       (some #(matches % req) routes))

     (defn serve-client [app main-js & [html-head]]
       ;; serve same JS for all client-side routes.
       (let [routes (b/get-setting app ::spa-routes)]
         (-> app
             (ring/serve-client (f/partial matches-any routes)
                                main-js html-head)
             (b/set-setting ::spa-routes nil))))))

(defn build [app & [options]]
  (-> app
      #?(:cljs (csr/build))
      #?(:clj (serve-client (get options :main-js "/js/main.js") (get options :html-head nil)))
      #?(:clj (ring/serve-resources "public"))))
