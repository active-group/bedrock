(ns bedrock.csr
  (:require [bedrock.core :as b]
            [reacl-c-basics.pages.core :as pcore]))

(defn serve [app route item-f]
  (-> app
      (b/update-setting ::csr-pages assoc route item-f)))

(defn build [app]
  (let [pages (b/get-setting app ::csr-pages)]
    (-> app
        (b/update-frontend (fn [item]
                             ;; TODO: warn if item was != nil ?
                             (pcore/html5-history-router pages)))
        (b/set-setting ::csr-pages nil))))
