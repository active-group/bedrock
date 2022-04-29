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

(defn- loading []
  [:div "Loading..."]
  ;; TODO: something nicer? https://www.w3schools.com/howto/howto_css_loader.asp
  #_[:div {:style "border: 16px solid #f3f3f3; border-top: 16px solid #3498db; border-radius: 50%; width: 120px; height: 120px"}])

#?(:clj
   (defn generate-main-html [main-js & [options]]
     (hiccup/html5 {}
                   (apply vector :head (:head options))
                   [:body
                    (vector :div {:id main-id}
                            (or (:loading options) (loading)))
                    (hiccup/include-js main-js)])))
