(ns bedrock.example.main
  (:require [bedrock.core :as b]
            #_[bedrock.example.hello-world :as e]
            [bedrock.example.hello-db :as e]))


#?(:cljs
   (b/start-frontend (e/app)))

#?(:clj
   (def handler
     (b/reloading-handler (e/app) {:dirs ["src" "dev"]})))
