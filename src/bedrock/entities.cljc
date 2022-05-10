(ns bedrock.entities
  (:require #?(:clj [bedrock.rest :as rest])
            [bedrock.spa :as spa]
            [active.clojure.functions :as f]
            #?(:cljs [bedrock.entities.frontend :as frontend])
            #?(:clj [bedrock.utils.jdbc :as jdbc])))

(defn entity-list [app path entity db]
  (let [api-route (str "/api/" (name entity))]
    (-> app
        (spa/page path
                  #?(:cljs (fn []
                             (frontend/entity-list api-route))))
        #?(:clj (rest/json-entity-list api-route
                                       (f/partial jdbc/get-entity-list db entity))))))

(defn editable-entity-list [app path entity schema db]
  (let [api-route (str "/api/" (name entity))]
    (-> app
        (spa/page path
                  #?(:cljs (fn []
                             (frontend/editable-entity-list api-route schema))))
        #?(:clj (rest/editable-json-entity-list api-route
                                                (f/partial jdbc/get-entity-list db entity)
                                                (f/partial jdbc/create-entity! db entity)
                                                (f/partial jdbc/get-entity db entity)
                                                (f/partial jdbc/put-entity! db entity)
                                                (f/partial jdbc/delete-entity! db entity))))))

