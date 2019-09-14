(ns examples.circle-area
  (:require [clj-see.program :as program])
  (:require [clojure.math.numeric-tower :refer [abs expt]]))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn sqr [x]
  (* x x))

;h(x=[1,2,3,4],y=4) = [-1.86, -1.2663, -0.857, -0.4725]
;h(x=[1,2,3,4],y=5) = [-2.34, -1.67, -1.231, -0.905]
;h(x=[1,2,3,4],y=6) = [-2.812, -2.059, -1.562, -1.1991]

(defn fitness [program]
  (let [program-size (count (program/all-paths program))
        differences [(Math/abs (- (program 1 4) -1.86))
                     (Math/abs (- (program 2 4) -1.2663))
                     (Math/abs (- (program 3 4) -0.857))
                     (Math/abs (- (program 4 4) -0.4725))
                     (Math/abs (- (program 1 5) -2.34))
                     (Math/abs (- (program 2 5) -1.67))
                     (Math/abs (- (program 3 5) -1.231))
                     (Math/abs (- (program 4 5) -0.905))
                     (Math/abs (- (program 1 6) -2.812))
                     (Math/abs (- (program 2 6) -2.059))
                     (Math/abs (- (program 3 6) -1.562))
                     (Math/abs (- (program 4 6) -1.199))]]
    (* program-size (- (apply + (map sqr differences))))))

(defmacro x [] `(first ~'args))

(defmacro y [] `(second ~'args))

(def all-mutations
  {:non-terminal [(fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (if (> (count _) 1) (rand-nth (rest _)) _))
                  (fn [_] (try (eval _) (catch Exception e _)))
                  (fn [_] `(+ ~_ 0))
                  (fn [_] `(- 0 ~_))
                  (fn [_] `(* ~_ 1))
                  (fn [_] `(Math/pow ~_ -1))
                  ;(fn [_] `(Math/acos ~_))
                  ;(fn [_] `(Math/asin ~_))
                  ;(fn [_] `(Math/atan ~_))
                  ;(fn [_] `(Math/cos ~_))
                  ;(fn [_] `(Math/cosh ~_))
                  (fn [_] `(Math/cbrt ~_))
                  (fn [_] `(Math/exp ~_))
                  (fn [_] `(Math/log ~_))
                  (fn [_] `(Math/sqrt ~_))]
   :terminal [(fn [_] (expt 2 (/ 1 (rand))))
              (fn [_] (expt 2 (/ 1 (rand))))
              (fn [_] (rand))
              (fn [_] (rand))
              (fn [_] `(x))
              (fn [_] `(x))
              (fn [_] `(y))
              (fn [_] `(y))
              (fn [_] `(+ ~_ 0))
              (fn [_] `(* ~_ 1))]})

(defn mutate-fn [expression]
  (let [mutations (if (seq? expression)
                    (all-mutations :non-terminal)
                    (all-mutations :terminal))]
    ((rand-nth mutations) expression)))
