(ns overspec.matchers
  (:use clojure.test))

(defn to-be [expected actual]
  (= actual expected))

(defn to-be-truthy [actual]
  (true? actual))

(defn to-be-falsy [actual]
  (false? actual))

(defn to-contain [key actual]
  (contains? actual key))

(defn to-be-nil [actual]
  (nil? actual))