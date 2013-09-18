overspec
========

Clojure port of Jasmine BDD (https://github.com/pivotal/jasmine).

Documentation can be found here http://pivotal.github.io/jasmine/

**Example of test specs**

```clojure
(deftest my-test
  (let [foo (atom nil)]

    (describe "A spec"
      (:before-each #(reset! foo 1))
      (:after-each #(reset! foo 0))

      (it "it can contain any code"
        (expect @foo (to-be 1)))

      (it "can have more than one expectation"
        (expect @foo (to-be 1))
        (expect true (to-be-truthy)))

      (let [bar (atom nil)]
        (describe "nested inside a second describe"
          (:before-each #(reset! bar 1))

          (it "can reference both scopes as needed"
            (expect @foo (to-be @bar))))))))
```

**Define your own matchers**

You could define your own matchers with defmatcher macro. Lets assume you want to make sure that expected value belongs to interval between a and b:

```clojure
(expect 3 (to-be-between 2 5))
```

In this case your custom matcher can be defined like this:
```clojure
(defmatcher to-be-between [a b actual]
  (and (< actual b) (> actual a)))
```

Argument "actual" should be the last one.

**Will be supported in future releases**

Spies (http://pivotal.github.io/jasmine/#section-Spies)