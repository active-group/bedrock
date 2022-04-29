(ns bedrock.example.main
  (:require [bedrock.core :as b]
            [bedrock.spa :as spa]
            [reacl-c.dom :as dom]))

(def hello-world
  (-> b/empty-app
      (spa/page "/" (constantly "Hello World"))
      (spa/build {:head-html [[:title "Hello World App"]]})))

(def app hello-world)

#?(:cljs
   (b/start-frontend app))

#?(:clj
   (def handler
     (b/reloading-handler app {:dirs ["src" "dev"]})))
