(ns clj-see.population-test
  (:require [clojure.test :refer :all]
            [clj-see.population :refer :all]
            [clj-see.program :as program]))

(deftest test-create-population
  (testing "New populations can be created"
    (is (= (create-population 2)
           {:programs [(program/create-program 0)
                       (program/create-program 0)]
            :size 2
            :iteration 0}))
    (is (= (create-population [(program/create-program 42)] 8)
           {:programs [(program/create-program 42)]
            :size 1
            :iteration 8}))))

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

(deftest test-create-population-filename-pattern
  (testing "Population filename pattern can be created"
    ; TODO: make this test black-box-style, maybe
    (is (= "prefix-([0-9]+).txt" (-> "prefix"
                                    create-population-filename-pattern
                                    str)))))

;(deftest test-serialization
;  (testing "Serializing and deserializing preserves a population"
;    (is (= (-> 10 create-population serialize deserialize)
;           (-> 10 create-population)))))
