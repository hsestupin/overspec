overspec
========

Clojure port of Jasmine BDD (https://github.com/pivotal/jasmine).

Documentation can be found here http://pivotal.github.io/jasmine/

### The Most Recent Version

``` clojure
[org.clojars.hsestupin/overspec "0.1.0"]
```

That release is stable but some functionality is not available yet.

### Before and After

A suite can have a :before-each declaration. It's a just a simple zero-arg function that is run before each spec belonged for `describe` scope. For example:

```clojure
(deftest my-test
  (let [suite-wide-foo (atom nil)]
    (describe "some suite"
      (:before-each #(reset! suite-wide-foo 1))
      (it "should equal 1"
        (expect @suite-wide-foo (to-be 1))))))
```

Similarly, there is an `after-each` declaration. It's also just a zero-arg function that is run after each spec belonged for `describe` scope. For example:

```clojure
(deftest my-test
  (let [suite-wide-foo (atom 1)]
    (describe "some suite"
      (:after-each #(reset! suite-wide-foo 0))
      (it "should equal 1"
        (expect @suite-wide-foo (to-be 1)))

      (it "should equal 0 after"
        (expect @suite-wide-foo (to-be 0))))))
```

A spec may require some code to be executed after the spec has finished running; the code will run whether the spec finishes successfully or not. This function is also zero-arg.

```clojure
(deftest my-test
  (let [suite-wide-foo (atom 1)]
    (describe "some suite"

      (it "should equal 1 and sets to 0 after"
        (expect @suite-wide-foo (to-be 1))
        (:after #(reset! suite-wide-foo 0)))

      (it "should equal 0 after"
        (expect @suite-wide-foo (to-be 0))))))
```

### Example of test specs

Specs could be nested as well. It works fine.

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

### Supported matchers

As far there are no lots of matchers supported by Overspec.

>`(expect x (to-be y))` passes if `(= x y)`=>`true`
>
>`(expect x (to-be-truthy))` passes if `(true? x)`=>`true`
>
>`(expect x (to-be-falsy))` passes if `(false? x)`=>`true`
>
>`(expect x (to-contain y))` passes if `(contains? x y)`=>`true`
>
>`(expect x (to-be-nil))` passes if `(nil? x)`=>`true`

### Define your own matchers

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
Note: defining matchers mechanism can be changed since I'm still working on library.

### Will be supported in future releases

- [Spies](http://pivotal.github.io/jasmine/#section-Spies)
- [Disabling specs and suites](http://pivotal.github.io/jasmine/#section-Disabling_Specs_and_Suites)