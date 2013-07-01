(ns clj-see.core-test
  (:require [clojure.test :refer :all]
            [clj-see.util :refer :all]))

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

(deftest test-list-get-in
  (is (= (list-get-in '(* 2 (+ 1 2)) ()) '(* 2 (+ 1 2))))
  (is (= (list-get-in '(* 2 (+ 1 2)) '(2)) '(+ 1 2)))
  (is (= (list-get-in '(* 2 (inc (inc 42))) '(2 1 1)) '42)))

(deftest test-list-assoc-in
  (is (= (list-assoc-in '(* 2 (+ 1 2))
                          '()
                          '(inc 42))
         '(inc 42)))
  (is (= (list-assoc-in '(* 2 (+ 1 2))
                          '(2 2)
                          '(/ 42 21))
         '(* 2 (+ 1 (/ 42 21))))))
