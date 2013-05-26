(ns clj-see.core-test
  (:require [clojure.test :refer :all]
            [clj-see.core :refer :all]))

(deftest test-prepend-paths
  (is (= (prepend-paths 3 '[()]) '[(3)]))
  (is (= (prepend-paths 3 '[() (1) (1 2 3)]) '[(3) (3 1) (3 1 2 3)])))

(deftest test-flatten-1
  (is (= (flatten-1 '((1) (1 2) (2))) '(1 1 2 2)))
  (is (= (flatten-1 '((1) ((1 2)) (2))) '(1 (1 2) 2))))

(deftest test-all-paths
  (is (= (all-paths '42) '[()]))
  (is (= (all-paths '(inc 42)) '[() (1)]))
  (is (= (all-paths '(+ 42 35)) '[() (1) (2)]))
  (is (= (all-paths '(+ 42 (something))) '[() (1) (2)]))
  (is (= (all-paths '(+ 42 (something else))) '[() (1) (2) (2 1)]))
  (is (= (all-paths '(+ 42 (* 7 8))) '[() (1) (2) (2 1) (2 2)])))

(deftest test-extract-snippet
  (is (= (extract-snippet '(* 2 (+ 1 2)) ()) '(* 2 (+ 1 2))))
  (is (= (extract-snippet '(* 2 (+ 1 2)) '(2)) '(+ 1 2)))
  (is (= (extract-snippet '(* 2 (inc (inc 42))) '(2 1 1)) '42)))

(deftest test-replace-snippet
  (is (= (replace-snippet '(* 2 (+ 1 2))
                          '()
                          '(inc 42))
         '(inc 42)))
  (is (= (replace-snippet '(* 2 (+ 1 2))
                          '(2 2)
                          '(/ 42 21))
         '(* 2 (+ 1 (/ 42 21))))))

(deftest test-crossover
  (is (= (crossover '(+ 1 (chiasma-1 42 (* 1 2)))
                    '(2)
                    '(* 2 3 (+ 1 (chiasma-2 123) 3))
                    '(3 2))
         '[(+ 1 (chiasma-2 123))
           (* 2 3 (+ 1 (chiasma-1 42 (* 1 2)) 3))])))

(deftest test-mutate
  (is (= (mutate '(+ (* a x x) (* b x) c)
                 '(1 3)
                 (fn [_] 'y))
         '(+ (* a x y) (* b x) c))))

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
