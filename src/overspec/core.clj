(ns overspec.core
  (:use clojure.test
        overspec.matchers
        overspec.utils))

(def ^:dynamic *before-fns* [])
(def ^:dynamic *after-fns* [])
(def ^:dynamic *within-spec?* false)
(def ^:dynamic *not?* false) ; it's true if core.clojure/not was used odd number of times

(defmacro when-within-spec [body]
  `(if *within-spec?*
     ~body
     (throw (IllegalArgumentException. "Spec is undefined"))))

(defmacro expect
  ([x matcher]
    `(expect ~x ~matcher nil))
  ([x matcher msg]
    (if (starts-with-not? matcher)
      `(when-within-spec
         (binding [*not?* (not *not?*)]
           (expect ~x ~@(rest matcher) ~msg)))
      `(when-within-spec
         (if *not?*
           (try-expr ~msg (not (~@matcher ~x)))
           (try-expr ~msg (~@matcher ~x)))))))

(defmacro it
  [string & body]
  (let [afters (filter #(= :after (first %)) body)
        others (remove #(= :after (first %)) body)
        finally-fn (sfirst afters)]
    (assert-size<=1 :after afters)

    `(let [current-before-fns# (remove nil? *before-fns*)
           current-after-fns# (remove nil? *after-fns*)]
       (when *within-spec?* (throw (IllegalArgumentException. "Nested (it) blocks are not allowed")))
       (binding [*testing-contexts* (conj *testing-contexts* ~string)
                 *within-spec?* true]
         (invoke-all current-before-fns#)
         (try
           (do ~@body)
           (finally
             ~(when finally-fn `(~finally-fn))))
         (invoke-all (reverse current-after-fns#)))))) ; invoke after-each fns in reverse order

(defmacro describe
  [string & body]
  (let [befores (filter #(= :before-each (first %)) body)
        afters (filter #(= :after-each (first %)) body)
        others (remove #(or (= :before-each (first %)) (= :after-each (first %))) body)]
    (assert-size<=1 :before-each befores)
    (assert-size<=1 :after-each afters)

    `(binding [*before-fns* (conj *before-fns* ~(sfirst befores))
               *after-fns* (conj *after-fns* ~(sfirst afters))
               *testing-contexts* (conj *testing-contexts* ~string)]
       ~@others)))