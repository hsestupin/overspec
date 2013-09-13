(ns overspec.matchers
  (:use clojure.test))

(def ^:dynamic *not?* false) ; it's true if core.clojure/not was used odd number of times

(defmacro defmatcher [name args pred]
  `(defn ~name [~@args msg#]
     (if *not?*
       (try-expr msg# (not ~pred))
       (try-expr msg# ~pred))))

(defmatcher to-be [expected actual]
  (= actual expected))

(defmatcher to-be-truthy [actual]
  (true? actual))

(defmatcher to-be-falsy [actual]
  (false? actual))

(defmatcher to-contain [key actual]
  (contains? actual key))

(defmatcher to-be-nil [actual]
  (nil? actual))