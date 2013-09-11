(ns overspec.core
  (:use [clojure.test]
        [overspec.utils]))

(def ^:dynamic *not?* false) ; it's true if core.clojure/not was used odd number of times
(def ^:dynamic *befores* [])
(def ^:dynamic *afters* [])
(def ^:dynamic *current-spec* nil)

(defmacro expect
  ([x expectation]
    `(expect ~x ~expectation nil))
  ([x expectation msg]
    (if (starts-with-not? expectation)
      `(binding [*not?* (not *not?*)]
         (expect ~x ~@(rest expectation) ~msg))
      `(~@expectation ~x ~msg))))

; MATCHERS

(defmacro to-be [expected actual msg]
  `(if *not?*
     (try-expr ~msg (~'not= ~actual ~expected))
     (try-expr ~msg (~'= ~actual ~expected))))

(defmacro to-be-truthy [actual msg]
  `(if *not?*
     (try-expr ~msg (~'false? ~actual))
     (try-expr ~msg (~'true? ~actual))))


(defmacro to-be-falsy [actual msg]
  `(if *not?*
     (try-expr ~msg (~'true? ~actual))
     (try-expr ~msg (~'false? ~actual))))

(defmacro to-be-nil [actual msg]
  `(if *not?*
     (try-expr ~msg (~'not (~'nil? ~actual)))
     (try-expr ~msg (~'nil? ~actual))))

(defmacro to-contain [key coll msg]
  `(if *not?*
     (try-expr ~msg (~'not (~'contains? ~coll ~key)))
     (try-expr ~msg (~'contains? ~coll ~key))))

(defmacro invoke-all [each-blocks]
  `(binding [*ns* (the-ns ~*ns*)]
     (doseq [each-block# ~each-blocks]
       (eval `(do ~@each-block#)))))

(defmacro it
  [string & body]
  `(let [current-befores# (remove nil? *befores*)
         current-afters# (remove nil? *afters*)]

     (binding [*testing-contexts* (conj *testing-contexts* ~string)]
;       (invoke-all current-befores#)
       (do ~@body)
;       (invoke-all (reverse current-afters#))
       ))) ;; invoke after-each blocks in reverse order

(defmacro describe
  [string & body]
  (let [befores (filter #(= :before-each (first %)) body)
        afters (filter #(= :after-each (first %)) body)
        others (remove #(or (= :before-each (first %)) (= :after-each (first %))) body)]
    (assert-each-block-count :before-each befores)
    (assert-each-block-count :after-each afters)

    (println "!befores" befores)
    `(binding [*befores* (conj *befores* (nfirst '~befores))
               *afters* (conj *afters* (nfirst '~afters))
               *testing-contexts* (conj *testing-contexts* ~string)]
       (do ~@others))))

(defmacro m [body]
  (let [a (doall (filter nil? '(1 2)))]
    `(println "a: " '~a)))