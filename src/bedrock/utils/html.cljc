(ns bedrock.utils.html
  #?(:clj (:require [active.clojure.functions :as f]
                    [hiccup.page :as hiccup])))

(def ^:private main-id "main")

#?(:cljs
   (defn get-main-node
     "Returns the node designated by [[generate-main-html]] to host the
  application."
     []
     (js/document.getElementById main-id)))

#?(:clj
   (do
     (defn head [& [title]]
       [:head (when title [:title title])])
   
     (defn generate-main-html [main-js & [head]]
       (hiccup/html5 {}
                     head
                     [:body
                      [:div {:id main-id}]
                      (hiccup/include-js main-js)]))))
