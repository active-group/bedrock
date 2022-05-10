(ns bedrock.rest
  (:require [bedrock.ring :as ring]
            [ring.util.http-response :as rur]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            #_[cognitect.transit :as transit])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

;; TODO maybe use liberator?

#_(defn- transit-json-response [v] ;; TODO: + handlers ?
  (let [strm (ByteArrayOutputStream. 4096)]
    (transit/write (transit/writer strm :json)
                   v)
    (-> (rur/ok (ByteArrayInputStream. (.toByteArray strm)))
        (rur/content-type "application/transit+json"))))

(defn- json-response [v]
  (-> (rur/ok (json/write-str v))
      (rur/content-type "application/json")))

(defn json-entity-list [app route get-list]
  (-> app
      (ring/serve-route :get route
                        (fn [_]
                          (json-response (get-list))))))

(defn- json-request-body [request]
  ;; TODO: bad requests?!
  ;; TODO: charset, options.
  (json/read (io/reader (:body request))))

(defn editable-json-entity-list [app route get-list create-item get-item put-item delete-item]
  (-> app
      (ring/serve-route :get route
                        (fn [_]
                          (json-response (get-list))))
      (ring/serve-route :post route
                        (fn [request]
                          (let [value (json-request-body request)
                                id (create-item value)]
                            (rur/created id))))
      (ring/serve-route :get (str route "/:id")
                        (fn [{{id :id} :route-params}]
                          (json-response (get-item id))))
      (ring/serve-route :put (str route "/:id")
                        (fn [{{id :id} :route-params :as request}]
                          (let [value (json-request-body request)]
                            (put-item id value))
                          (rur/ok)))
      (ring/serve-route :delete (str route "/:id")
                        (fn [{{id :id} :route-params}]
                          (delete-item id)
                          (rur/ok)))
      ))

