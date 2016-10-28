overspec
========

Clojure port of Jasmine BDD (https://github.com/jasmine/jasmine).

Documentation can be found here http://jasmine.github.io/

### The Most Recent Version

``` clojure
[org.clojars.hsestupin/overspec "0.1.1"]
```


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

Specs could be nested as well. It works fine. Clojure original scope rules are applied.

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

If you need you could define your own matchers. Lets assume you want to make sure that expected value belongs to interval between a and b:

```clojure
(expect 3 (to-be-between 2 5))
```

In this case your custom matcher can be defined like simple predicate. if that function returns true than spec passes.
```clojure
(defn to-be-between [a b actual]
  (and (< actual b) (> actual a)))
```

Argument "actual" should be the last one.

### Disabling Specs and Suites

Suites and specs can be disabled with the `xdescribe` and `xit` functions, respectively. These suites and specs are skipped when run and thus their results will not appear in the results.

### Will be supported in future releases

- [Spies](http://pivotal.github.io/jasmine/#section-Spies) ( I'm still not sure, I doubt that true functional style shouldn't support building some expectations about calling functions)
