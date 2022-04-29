(ns bedrock.utils.ring-adapter
  (:require [ring.util.response :as resp]
            bedrock.utils.jetty-logging ;; must be imported before jetty
            [ring.adapter.jetty :refer [run-jetty]]))

(def empty-handler
  (constantly (resp/not-found "Not found")))

(defn run [handler & [options]]
  (run-jetty handler {:port (get options :port 8080) :join? false}))
