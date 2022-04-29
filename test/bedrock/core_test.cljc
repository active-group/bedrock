(ns bedrock.core-test
  (:require #?(:clj [clojure.test :refer (is deftest testing)])
            #?(:cljs [cljs.test :refer (is deftest testing) :include-macros true])))

(deftest dummy-test
  (is true))
