(ns bedrock.csr
  "Builds a frontend from multiple pages with client-side routing.

  You usually don't need to use this namespace
  directly. Use [[bedrock/spa]] for building backend and frontend
  togehter.
  "
  (:require [bedrock.core :as b]
            [reacl-c-basics.pages.core :as pcore]))

(defn serve
  "Add serving the given route pattern with the given function, which is
  called with the arguments from the url and must return a reacl-c
  item.

  Note: After adding all routes, you must call [[build]] once.
  "
  [app route item-f]
  (-> app
      (b/update-setting ::csr-pages assoc route item-f)))

(defn build
  "Replaces the frontend of the given app with a client side router,
  that serves all pages previously registered via [[serve]]."
  [app]
  (let [pages (b/get-setting app ::csr-pages)]
    (-> app
        (b/update-frontend (fn [item]
                             ;; TODO: warn if item was != nil ?
                             ;; TODO: add 'not found' page for unknown routes?
                             (pcore/html5-history-router pages)))
        (b/set-setting ::csr-pages nil))))
