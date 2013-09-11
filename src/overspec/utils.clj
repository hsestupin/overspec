(ns overspec.utils)

(defn starts-with? [form el]
  (= el (eval `#'~(first form))))

(defn starts-with-not? [form]
  (starts-with? form #'clojure.core/not))

(defn eval-each [forms]
  (doseq [form# forms]
    (eval `(do ~@form#))))

(defn assert-each-block-count
  [each-keyword each-blocks]
  (when (> (count each-blocks) 1)
    (throw (IllegalArgumentException. (str (name each-keyword) " can't be used more than once")))))