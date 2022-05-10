(ns bedrock.ring
  (:require [bedrock.core :as b]
            [bedrock.utils.html :as html]
            [active.clojure.functions :as f]
            [ring.util.response :as rur]
            [ring.middleware.content-type :as content-type]
            [ring.middleware.resource :as resource]
            [compojure.core :as compojure]))

(let [g (fn [handler pred f req]
          (if-let [v (pred req)]
            (f v)
            (handler req)))]
  (defn serve [app pred f]
    (-> app
        (b/update-backend (fn [handler]
                            (f/partial g handler pred f))))))

(let [g (fn [handler r req]
          (or (r req) (handler req)))]
  (defn serve-routes [app & routes]
    (let [r (apply compojure/routes routes)]
      (-> app
          (b/update-backend (fn [handler]
                              (f/partial g handler r)))))))

(defn serve-route [app method path f]
  (-> app
      (serve-routes (compojure/make-route method path f))))

(defn serve-const [app pred resp]
  (-> app
      (serve pred (f/constantly resp))))

(defn serve-const-html [app pred html]
  (-> app
      (serve-const pred (-> (rur/response html)
                            (rur/content-type "text/html")))))

(defn serve-js-app [app pred main-js & [options]]
  (-> app
      (serve-const-html pred (html/generate-main-html main-js {:head (:head-html options)
                                                               :loading (:loading-html options)}))))

(defn serve-resources [app path & [options]]
  (-> app
      (b/update-backend
       (fn [handler]
         (-> handler
             (resource/wrap-resource path (dissoc options :mime-types))
             (content-type/wrap-content-type {:mime-types (:mime-types options)}))))))
