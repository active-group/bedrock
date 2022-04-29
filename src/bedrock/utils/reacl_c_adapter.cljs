(ns bedrock.utils.reacl-c-adapter
  (:require [reacl-c.main :as reacl-c]
            [bedrock.utils.html :as html]))

(def empty-item nil)

(defn start [item & [options]]
  (let [node (get options :node (html/get-main-node))]
    (reacl-c/run node item options)))
