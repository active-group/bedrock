(ns bedrock.example.hello-world
  (:require [bedrock.core :as b]
            [bedrock.spa :as spa]))

(defn app []
  (-> b/empty-app
      (spa/page "/" (constantly "Hello World"))
      (spa/build {:head-html [[:title "Hello World App"]]})))
