(ns bedrock.entities.frontend
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c.dom :as dom :include-macros true]
            [reacl-c-basics.forms :as forms]
            [reacl-c-basics.ajax :as ajax]
            [ajax.json :as ajax-json]
            [active.clojure.lens :as lens]
            [active.clojure.functions :as f]))

(defn schema? [v]
  (and (map? v) (contains? v ::output)))

(defn entity-schema
  ([output]
   {::output output})
  ([empty output input]
   {::empty empty
    ::output output
    ::input input}))

(defn generic-schema [keys-types]
  (entity-schema
   (into {}
         (map (fn [[key type]]
                [key (case type
                       :str "")])
              keys-types))
   (fn [value]
     (apply dom/div
            (map (fn [[key type]]
                   (let [value (get value key)]
                     (dom/div (dom/div (name key))
                              (dom/div (str value)))))
                 keys-types)))
   (apply c/fragment
          (map (fn [[key type]]
                 (c/focus key (dom/div (dom/label (name key))
                                       (case type
                                         :str (forms/input-string)))))
               keys-types))))

(defn derive-schema [empty]
  (generic-schema (map (fn [[k v]]
                         [k (condp = v
                              "" :str)])
                       empty)))

(defn- empty-from-schema [schema]
  (::empty schema))

(defn- inputs-from-schema [schema]
  (::input schema))

(defn- show-from-schema [schema value]
  ((::output schema) value))

(def ^:private json-request-format (ajax-json/json-request-format))

(c/defn-item entity-list :static [route schema]
  (c/isolate-state
   nil
   (c/fragment (ajax/fetch (ajax/GET route {:keywords? true
                                            :response-format :json}))
               (c/dynamic (fn [res]
                            (cond
                              (nil? res)
                              "Loading..."
                                              
                              (ajax/response-ok? res)
                              (show-from-schema schema (ajax/response-value res))

                              :else
                              (throw (ajax/response-value res))))))))


(defn- entity-server-action-form [initial schema mk-request job-id & content]
  (c/local-state initial
                 (dom/form {:onsubmit (fn [[state value] ev]
                                        (.preventDefault ev)
                                        (c/return :state [state initial]
                                                  :action (ajax/deliver (mk-request value) job-id)))}
                           (c/focus lens/second (inputs-from-schema schema))
                           (c/focus lens/first (apply c/fragment content)))))

(defn- add-entity-form [route schema job-id]
  (entity-server-action-form (empty-from-schema schema)
                             schema
                             (fn [value]
                               (ajax/POST route {:params value
                                                 :format json-request-format}))
                             job-id
                             (dom/button {:type "submit"} "Add")))

(defn- edit-entity-form [route schema value job-id]
  (entity-server-action-form value
                             schema
                             (fn [value]
                               (ajax/PUT route {:params value
                                                :format json-request-format}))
                             job-id
                             (dom/button {:type "submit"} "Save")))

(c/defn-item ^{:private true} editable-entity :static [route schema value update-job-id delete-job-id]
  (c/with-state-as [_ local :local {:value value ;; <- reset local state when value changes.
                                    :editing? false}]
    (if (:editing? local)
      (edit-entity-form route schema value update-job-id)
      (dom/div (show-from-schema schema value)
               (dom/button {:type "button"
                            :onclick (fn [[state lstate] _]
                                       [state (assoc lstate :editing? true)])}
                           "Edit")
               (dom/button {:type "button"
                            :onclick (f/constantly
                                       (c/return :action (ajax/deliver (ajax/DELETE route) delete-job-id)))}
                           "Delete")))))

(c/defn-item editable-entity-list :static [route schema]
  ;; TODO: allow to modify layout; buttons; labels?
  (c/isolate-state
   nil
   (ajax/delivery
    (c/fragment
     (c/init (c/return :action (ajax/deliver (ajax/GET route {:keywords? true
                                                              :response-format :json})
                                             :init)))
     (ajax/delivery
      (dom/div (add-entity-form route schema :add)
               (c/dynamic (fn [res]
                            (cond
                              (nil? res)
                              "Loading..."
                                              
                              (ajax/response-ok? res)
                              (apply dom/div ;; TODO: suitable wrapper for all from schema?
                                     (map (fn [[id value]]
                                            (editable-entity (str route "/" id) schema value
                                                             :update :delete))
                                          (ajax/response-value res)))

                              :else
                              (throw (ajax/response-value res))))))
      (fn [state job]
        ;; TODO: disable everything while job pending
        ;; TODO: errors
        (case (ajax/delivery-job-status job)
          :completed
          (case (ajax/delivery-job-info job)
            (:add
             :update
             :delete)
            (c/return :action (ajax/deliver (ajax/GET route {:keywords? true
                                                             :response-format :json})
                                            :reload)))

          (c/return)))))
    (fn [state job]
      ;; TODO: disable everything while job pending
      ;; TODO: errors
      (case (ajax/delivery-job-status job)
        :completed
        (case (ajax/delivery-job-info job)
          (:init
           :reload)
          (c/return :state (ajax/delivery-job-response job)))

        (c/return))))))
