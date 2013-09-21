(ns overspec.utils)

(defn starts-with? [form symb]
  (= symb (resolve (first form))))

(defn starts-with-not? [form]
  (starts-with? form #'clojure.core/not))

(defn assert-size<=1
  [each-keyword each-blocks]
  (when (> (count each-blocks) 1)
    (throw (IllegalArgumentException. (str (name each-keyword) " can't be used more than once")))))

(defn sfirst [forms]
  (second (first forms)))

(defn invoke-all [fns]
  (doseq [fn fns]
    (fn)))