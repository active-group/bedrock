(ns bedrock.spa
  "Builds a so called single page application by using client-side
  routing to create an application from multiple pages."
  
  (:require [bedrock.core :as b]
            #?(:clj [bedrock.ring :as ring])
            #?(:cljs [bedrock.csr :as csr])
            [active.clojure.functions :as f]
            [reacl-c-basics.pages.routes :as routes]))

(defn page
  "Adda a page with the given route pattern and the given function,
  which is called with the arguments from the url and must return a
  reacl-c item.

  Note: After adding all pages, you must call [[build]] once.
  "
  #?(:clj ([app route]
           (page app route (f/constantly nil))))
  ([app route item-f]
   (let [route (if (string? route) (routes/route route) route)]
     (-> app
         (b/update-setting ::spa-routes conj route)
         #?(:cljs (csr/serve route item-f))))))

#?(:clj
   (do
     (defn- matches [route req]
       (routes/route-matches route req))
    
     (defn- matches-any [routes req]
       (some #(matches % req) routes))

     (defn serve-client
       "Adds backend routes for all page routes, responding with a html page
  that starts the compiled frontend."
       [app & [options]]
       ;; serve same JS for all client-side routes.
       (let [main-js (or (:main-js options) "/js/main.js")
             routes (b/get-setting app ::spa-routes)]
         (-> app
             (ring/serve-js-app (f/partial matches-any routes)
                                main-js (dissoc options :main-js))
             (b/set-setting ::spa-routes nil))))))

(defn build
  "Replaces the frontend with a client-side router for all pages
  previously registered via [[page]], backend routes to serve a html
  page that starts the compiled frontend, and routes for additional
  public resources."
  [app & [options]]
  (-> app
      #?(:cljs (csr/build))
      #?(:clj (serve-client {:main-js (:main-js options)
                             :loading-html (:loading-html options)
                             :head-html (:head-html options)}))
      #?(:clj (ring/serve-resources "public" (dissoc options :main-js :html-head)))
      ))
