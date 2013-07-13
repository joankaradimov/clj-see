(ns clj-see.program-test
  (:require [clojure.test :refer :all]
            [clj-see.program :refer :all]))

(deftest test-crossover
  (testing "Crossover function can be called for programs"
    (is (= (crossover (create-program 'x) (create-program 'x))
           [(create-program 'x) (create-program 'x)]))))

(deftest test-mutate
  (testing "Mutate function can be called for programs"
    (mutate (create-program '(+ a b)) identity)))

(deftest test-ifn-implementation
  (testing "Programs can be invoked"
    (is (= ((create-program '(+ (first args) (second args))) 40 2)
           42)))
  (testing "Programs can be invoked with up to 6 arguments"
    (is (= ((create-program '(apply + args))) 0))
    (is (= ((create-program '(apply + args)) 1) 1))
    (is (= ((create-program '(apply + args)) 1 2) 3))
    (is (= ((create-program '(apply + args)) 1 2 3) 6))
    (is (= ((create-program '(apply + args)) 1 2 3 4) 10))
    (is (= ((create-program '(apply + args)) 1 2 3 4 5) 15))
    (is (= ((create-program '(apply + args)) 1 2 3 4 5 6) 21)))
  (testing "Programs can have an argument list applied to them"
    (is (= (apply (create-program '(apply + args)) [1 2]) 3))))
