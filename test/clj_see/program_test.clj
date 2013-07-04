(ns clj-see.program-test
  (:require [clojure.test :refer :all]
            [clj-see.program :refer :all]))

(deftest test-crossover
  (testing "Crossover function can be called for programs"
    (is (= (crossover (create-program 'x) (create-program 'x))
           [(create-program 'x) (create-program 'x)]))))

(deftest test-mutate
  (testing "Mutate function can be called for programs"
    (is (= (mutate (create-program '(+ a b)) (fn [x] x))
           (create-program '(+ a b))))))
