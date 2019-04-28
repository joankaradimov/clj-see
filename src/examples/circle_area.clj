(ns examples.circle-area
  (:require [clojure.math.numeric-tower :refer [abs expt]]))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn sqr [x]
  (* x x))

(defn sigmoid [x]
  (/ 1 (+ 1 (expt Math/E (- x)))))

(defn fitness [program]
  (let [diff-fn #(abs (- (circle-area %) (program %)))
        differences (map diff-fn (range 10))]
    (- (apply + (map sqr differences)))))

(defmacro r [] `(first ~'args))

(def all-mutations
  {:non-terminal [(fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] `(+ ~_ 0))
                  (fn [_] `(* ~_ 1))]
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
