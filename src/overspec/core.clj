(ns overspec.core
  (:use clojure.test
        overspec.matchers
        overspec.utils))

(def ^:dynamic *before-fns* [])
(def ^:dynamic *after-fns* [])
(def ^:dynamic *within-spec?* false)

(defmacro when-within-spec [body]
  `(if *within-spec?*
    ~body
    (throw (IllegalArgumentException. "Spec is undefined"))))

(defmacro expect
  ([x expectation]
    `(expect ~x ~expectation nil))
  ([x expectation msg]
    (if (starts-with-not? expectation)
      `(when-within-spec
         (binding [overspec.matchers/*not?* (not overspec.matchers/*not?*)]
           (expect ~x ~@(rest expectation) ~msg)))
      `(when-within-spec
         (~@expectation ~x ~msg)))))

(defmacro it
  [string & body]
  `(let [current-before-fns# (remove nil? *before-fns*)
         current-after-fns# (remove nil? *after-fns*)]
     (when *within-spec?* (throw (IllegalArgumentException. "Nested (it) blocks are not allowed")))
     (binding [*testing-contexts* (conj *testing-contexts* ~string)
               *within-spec?* true]
       (invoke-all current-before-fns#)
       (do ~@body)
       (invoke-all (reverse current-after-fns#))))) ; invoke after-each fns in reverse order

(defmacro describe
  [string & body]
  (let [befores (filter #(= :before-each (first %)) body)
        afters (filter #(= :after-each (first %)) body)
        others (remove #(or (= :before-each (first %)) (= :after-each (first %))) body)]
    (assert-each-block-count :before-each befores)
    (assert-each-block-count :after-each afters)

    `(binding [*before-fns* (conj *before-fns* ~(second (first befores)))
               *after-fns* (conj *after-fns* ~(second (first afters)))
               *testing-contexts* (conj *testing-contexts* ~string)]
       ~@others)))