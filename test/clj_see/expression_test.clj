(ns clj-see.expression-test
  (:require [clojure.test :refer :all]
            [clj-see.expression :refer :all]))

(deftest test-crossover
  (testing "Crossover function"
    (testing "can swap subexpressions with given paths"
      (is (= (crossover '(+ 1 (chiasma-1 42 (* 1 2)))
                        '(2)
                        '(* 2 3 (+ 1 (chiasma-2 123) 3))
                        '(3 2))
             '[(+ 1 (chiasma-2 123))
               (* 2 3 (+ 1 (chiasma-1 42 (* 1 2)) 3))])))
    (testing "can be called without paths"
      (is (= (crossover 'x 'x)
             '[x x])))))

(deftest test-mutate
  (testing "Mutate function"
    (testing "can change a subexpression with a given path"
      (is (= (mutate '(+ (* a x x) (* b x) c)
                     '(1 3)
                     (fn [_] 'y))
             '(+ (* a x y) (* b x) c))))
    (testing "can be called without a path"
      (is (= (mutate '(+ a b) (fn [x] x))
             '(+ a b))))))
