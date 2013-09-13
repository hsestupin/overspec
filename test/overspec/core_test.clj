(ns overspec.core-test
  (:use clojure.test
        overspec.core
        overspec.matchers
        overspec.other-ns))

(deftest not-test
  (describe "FIXME, Me fail TOO."
    (it "defining spec"
      (expect (+ 1 3) (not (to-be 5)) "4 should not equal to 5"))))

(defn printa []
  (println "a"))
(defn printb []
  (println "b"))

(deftest b-test
  ;  (let [a 3]
  ;
  ;    (describe "mmm"
  ;
  ;      (:before-each (do (println "abc")))
  ;      (:after-each #(do (println "abc") %1))
  ;      ;      (it "qq" (println "ff"))
  ;      (it "ff"
  ;        (println "fffff"))
  ;      (println "ab")))

  (let [b "bef"]
    (describe "what a describing"
      (:before-each (do
                      (printa)
                      (println-from-other-ns)
                      (fn [] (println b))
                      ))
      (:after-each (do
                     (println "c")
                     (println "b")))

      (it "what an it block"
        (let [a 3]
          (expect a (to-be 5) "a is 3"))
        (expect (+ 2 3) (to-be (- 6 1)) "5 is 5"))
      (it "success it"
        (println "i am inside"))

      (describe " nested describe "
        (:before-each (println "nested before"))
        (:after-each (println "nested after"))

        (it "description nested it"
          (println "nested it"))

        (it "failing nested it"
          (expect 3 (to-be-falsy)))))))

(deftest matchers-test
  (describe "testing"
    (it "matchers"
      (expect (= 1 1) (to-be-truthy))
      (expect false (to-be-falsy))
      (expect nil (to-be-nil)))))