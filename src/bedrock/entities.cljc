(ns bedrock.entities
  (:require #?(:clj [bedrock.rest :as rest])
            [bedrock.spa :as spa]
            [active.clojure.functions :as f]
            #?(:cljs [bedrock.entities.frontend :as frontend])
            #?(:clj [bedrock.utils.jdbc :as jdbc])))

#?(:cljs (defn- to-schema [v]
           (if (frontend/schema? v)
             v
             ;; v as an 'example value'
             (frontend/derive-schema v))))

(defn entity-list [app path entity db schema]
  (let [api-route (str "/api/" (name entity))]
    (-> app
        (spa/page path
                  #?(:cljs (fn []
                             (frontend/entity-list api-route (to-schema schema)))))
        #?(:clj (rest/json-entity-list api-route
                                       (f/partial jdbc/get-entity-list db entity))))))

(defn editable-entity-list [app path entity db schema]
  (let [api-route (str "/api/" (name entity))]
    (-> app
        (spa/page path
                  #?(:cljs (fn []
                             (frontend/editable-entity-list api-route (to-schema schema)))))
        #?(:clj (rest/editable-json-entity-list api-route
                                                (f/partial jdbc/get-entity-list db entity)
                                                (f/partial jdbc/create-entity! db entity)
                                                (f/partial jdbc/get-entity db entity)
                                                (f/partial jdbc/put-entity! db entity)
                                                (f/partial jdbc/delete-entity! db entity))))))

