(ns overspec.core-test
  (:use clojure.test
        overspec.core
        overspec.matchers
        overspec.other-ns))

(defmacro should-fail [body]
  `(let [report-type# (atom nil)]
     (binding [clojure.test/report #(reset! report-type# (:type %))]
       ~body)
     (testing "should fail"
       (is (= @report-type# :fail )))))

(deftest matchers-test
  (describe "testing matchers:"

    (it "to-be"
      (let [x 3]
        (is (= x 3))
        (expect x (to-be 3))
        (should-fail (expect x (to-be 4)))))

    (it "to-be-truthy"
      (let [x (= 1 1)]
        (is (true? x))
        (expect x (to-be-truthy))
        (should-fail (expect (not x) (to-be-truthy)))))

    (it "to-be-falsy"
      (let [x (= 1 2)]
        (is (false? x))
        (expect x (to-be-falsy))
        (should-fail (expect (not x) (to-be-falsy)))))

    (it "to-be-nil"
      (let [x nil]
        (is (nil? x))
        (expect nil (to-be-nil))
        (should-fail (expect 5 (to-be-nil)))))

    (it "to-contain"
      (let [x #{:a :b :c }]
        (is (contains? x :a ))
        (expect x (to-contain :a ))
        (should-fail (expect x (to-contain :d ))))

      (let [x {:a "a" :b "b"}]
        (is (contains? x :a ))
        (expect x (to-contain :a ))
        (should-fail (expect x (to-contain :c ))))

      (let [x [:a :b ]]
        (is (contains? x 1))
        (expect x (to-contain 1))
        (should-fail (expect x (to-contain 2)))))

    (it "with negation"
      (expect 1 (not (to-be 2)))
      (expect 1 (not (not (to-be 1))))
      (expect 1 (not (not (not (to-be 2))))))))

(deftest nested-spec-test
  (let [executed-code (atom #{})]
    (describe "Global describe starts."
      (swap! executed-code conj :global-before-spec )

      (it "Global spec."
        (swap! executed-code conj :global-inside-spec ))

      (describe "Nested decribe start."
        (swap! executed-code conj :nested-beyond-spec )
        (it "Nested spec."
          (swap! executed-code conj :nested-inside-spec )))

      (swap! executed-code conj :global-after-spec ))

    (is (= @executed-code
            #{:global-inside-spec :global-before-spec :global-after-spec :nested-beyond-spec :nested-inside-spec }))))

(deftest before&after-each-test
  (let [state (atom [])]
    (describe "<describe 1>"
      (:before-each #(swap! state conj :before-1 ))
      (:after-each #(swap! state conj :after-1 ))
      (is (empty? @state))
      (it "<spec 1>"
        (is (= @state [:before-1 ])))
      (is (= @state [:before-1 :after-1 ]))

      (describe "<describe 2>"
        (:before-each #(swap! state conj :before-2 ))
        ;        after-each block is missing by design
        (swap! state empty)
        (it "<spec 2>"
          (is (= @state [:before-1 :before-2 ])))
        (is (= @state [:before-1 :before-2 :after-1 ]))

        (describe "<describe 3>"
          (:before-each #(swap! state conj :before-3 ))
          (:after-each #(swap! state conj :after-3 ))

          (swap! state empty)
          (it "<spec 3 with finally block>"
            (:after #(swap! state conj :after ))
            (is (= @state [:before-1 :before-2 :before-3 ])))

          (is (= @state [:before-1 :before-2 :before-3 :after :after-3 :after-1 ])))))))


(deftest invalid-expect-usage-test
  (describe "expect should be called inside (it) block"
    (is (thrown-with-msg? IllegalArgumentException #"Spec is undefined" (expect true (to-be-truthy))))))

(deftest my-test
  (let [foo (atom nil)]

    (describe "A spec"
      (:before-each #(reset! foo 1))
      (:after-each #(reset! foo 0))

      (it "it can contain any code"
        (expect @foo (to-be 1)))

      (it "can have more than one expectation"
        (expect @foo (to-be 1))
        (:after #(println "spec is closed"))
        (expect true (to-be-truthy)))

      (let [bar (atom nil)]
        (describe "nested inside a second describe"
          (:before-each #(reset! bar 1))

          (it "can reference both scopes as needed"
            (expect @foo (to-be @bar))))))))

(deftest my-test
  (let [suite-wide-foo (atom 1)]
    (describe "some suite"

      (it "should equal 1 and sets to 0 after"
        (expect @suite-wide-foo (to-be 1))
        (:after #(reset! suite-wide-foo 0)))

      (it "should equal 0 after"
        (expect @suite-wide-foo (to-be 0))))))
