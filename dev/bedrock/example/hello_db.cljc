(ns bedrock.example.hello-db
  (:require [bedrock.core :as b]
            [bedrock.spa :as spa]
            [reacl-c.dom :as dom]
            [bedrock.entities :as entites]
            #?(:clj [bedrock.utils.jdbc :as jdbc])))

(defn app []
  (let [db (let [db {:connection-uri "jdbc:h2:mem:bedrock_example;DB_CLOSE_DELAY=-1"}]
             #?(:clj (jdbc/create-entity-table! db :people))
             db)]
    (-> b/empty-app
        (entites/editable-entity-list "/" :people [[:name :str]] db)
        (spa/build {:head-html [[:title "Hello Entites App"]]}))))
