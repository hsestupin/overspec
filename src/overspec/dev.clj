(ns user
  (:use overspec.core
        overspec.utils))

(let [afn (fn [] (print "bef"))
      state (atom #{})]
  (describe "abc"
    (:before-each #(swap! state conj :before))
    (:after-each #(swap! state disj :before))

    (swap! state conj :before-it)
    (it "str"
      (expect @state (to-contain :before)))
    (println "after it" state)
;    (expect @state (to-contain :before))
    ))

(defmacro m []
  `(defn bullshit [] (println "abc")))
