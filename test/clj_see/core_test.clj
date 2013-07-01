(ns clj-see.util-test
  (:require [clojure.test :refer :all]
            [clj-see.core :refer :all]))

(deftest test-form-pairs
  (testing "Split population into pairs"
    (testing "with even number of expressions"
      (is (= (form-pairs [1 2 3 4 5 6]) [[1 2] [3 4] [5 6]])))
    (testing "with odd number of expressions"
      (is (= (form-pairs [1 2 3 4 5 6 7]) [[1 2] [3 4] [5 6]])))))

(deftest test-take-fittest
  (testing "Sorts expressions by their fitness"
    (is (= (take-fittest [1 2 5 3 6 4] identity 6)
           [6 5 4 3 2 1])))
  (testing "Preserves identical expressions"
    (is (= (take-fittest [1 1 1] identity 3)
           [1 1 1])))
  (testing "Keeps only part of the population"
    (is (= (take-fittest [1 1 1 1] identity 2)
           [1 1]))))
