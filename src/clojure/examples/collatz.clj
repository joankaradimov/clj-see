(ns examples.collatz
  (:require [clj-see.program :as program])
  (:require [clojure.math.numeric-tower :refer [abs expt]]))

(defn sqr [x]
  (* x x))

(defn relu [x]
  (if (pos? x) x 0))

(defn fitness [program]
  (let [program-size (count (program/all-paths program))
        diff-fn #(abs (- (examples.CollatzDegree/degree %) (program %)))
        differences (map diff-fn (range 10))]
    (* program-size (- (apply + (map sqr differences))))))

(defmacro r [] `(first ~'args))

(def all-mutations
  {:non-terminal [(fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (try (eval _) (catch Exception e _)))
                  (fn [_] `(+ ~_ 0))
                  (fn [_] `(- 0 ~_))
                  (fn [_] `(* ~_ 1))
                  (fn [_] `(relu ~_))
                  (fn [_] `(Math/floor ~_))
                  (fn [_] `(Math/floor ~_))
                  (fn [_] `(Math/ceil ~_))
                  (fn [_] `(Math/pow ~_ -1))
                  (fn [_] `(Math/cbrt ~_))
                  (fn [_] `(Math/exp ~_))
                  (fn [_] `(Math/log ~_))
                  (fn [_] `(Math/sqrt ~_))]
   :terminal [(fn [_] (expt 2 (/ 1 (rand))))
              (fn [_] (expt 2 (/ 1 (rand))))
              (fn [_] (rand))
              (fn [_] (rand))
              (fn [_] `(r))
              (fn [_] `(+ ~_ 0))
              (fn [_] `(* ~_ 1))]})

(defn mutate-fn [expression]
  (let [mutations (if (seq? expression)
                    (all-mutations :non-terminal)
                    (all-mutations :terminal))]
    ((rand-nth mutations) expression)))
