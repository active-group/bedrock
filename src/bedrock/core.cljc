(ns bedrock.core
  (:require #?(:cljs [bedrock.utils.reacl-c-adapter :as reacl-c-adapter])
            #?(:clj [bedrock.utils.ring-adapter :as ring-adapter])
            #?(:clj [ring.middleware.reload :as rmr])))

(defrecord App [settings frontend backend]
  ;; settings: {}
  ;; frontent: reacl-c item
  ;; backend: ring-handler
  )

(defn update-backend [app f & args]
  (apply update app :backend f args))

(defn update-frontend [app f & args]
  (apply update app :frontend f args))

(defn update-settings [app f & args]
  (apply update app :settings f args))

(defn update-setting [app key f & args]
  (update-settings app
                   (fn [m]
                     (apply update m key f args))))

(defn set-setting [app key value]
  (update-settings app
                   (fn [m]
                     (assoc m key value))))

(defn get-setting [app key & [dflt]]
  (get (:settings app) key dflt))


(defn handler
  "Returns a ring handler for the given app."
  [app]
  (:backend app))

#?(:clj
   (defmacro reloading-handler
     "Returns a ring handler for the given app, with hot-code reloading
  enabled. Use [[handler]] for production code. Options are that
  of [[ring.middleware.reload/wrap-reload]]."
     [app & [options]]
     ;; Note: adding an indirection so that the 'same' handler sees a redefined app; which is also why this has to be a macro.
     `(-> (fn [req#]
            ((handler ~app) req#))
          (rmr/wrap-reload ~options))))

#?(:clj
   (defn run-server [app & [options]]
     (ring-adapter/run (handler app) options)))

#?(:cljs
   (defn start-frontend [app & [options]]
     (reacl-c-adapter/start (:frontend app) options)))

(def empty-app
  (App. {}
        (do #?(:cljs reacl-c-adapter/empty-item))
        (do #?(:clj ring-adapter/empty-handler))))
