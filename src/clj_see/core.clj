(ns clj-see.core
  (:require clj-see.util clj-see.expression))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn abs [x]
  (if (neg? x) (- x) x))

(defn circle-area-fitness [expression]
  (let [expression-fn (clj-see.expression/expression->fn expression)
        differences (for [r (range 5)] (abs (- (circle-area r)
                                                (expression-fn r))))]
    (- (apply + (map #(* % %) differences)))))

(defn form-pairs [expressions]
  (loop [remaining-exps expressions
         accumulator ()]
    (let [first-exp (first remaining-exps)
          second-exp (second remaining-exps)]
      (if (and first-exp second-exp)
        (recur (-> remaining-exps rest rest)
               (conj accumulator [first-exp second-exp]))
        (reverse accumulator)))))

; TODO: fitness can be cached, probably
(defn take-fittest [expressions fitness-function count]
  (->> expressions
       (sort-by fitness-function >)
       (take count)))

(defn next-generation [expressions elitism-factor]
  (let [population-count (count expressions)
        old-expression-count (* elitism-factor population-count)
        new-expression-count (- population-count old-expression-count)
        new-expressions (->> expressions
                             form-pairs
                             (map #(apply clj-see.expression/crossover %))
                             clj-see.util/flatten-1)]
    (concat (take-fittest expressions
                          circle-area-fitness
                          old-expression-count)
            (take-fittest new-expressions
                          circle-area-fitness
                          new-expression-count))))

(def initial-population `[~Math/PI
                          ~'r
                          (+ 0 0)
                          (* 1 1)
                          (- 0 0)
                          ~Math/PI
                          (+ 0 0)
                          (* 1 1)
                          (+ 0 0)
                          (* 1 1)
                          ~Math/PI
                          ~'r
                          (+ 2 0)
                          (* 2 1)
                          (- 1 1)
                          ~Math/PI
                          (* 1 0)
                          (* 1 1)
                          (* 0 0)
                          (* 1 1)])

(defn -main []
  (loop [population initial-population
         iteration 0]
    (prn iteration population)
    (if (< iteration 10)
      (recur (next-generation population 0.1) (inc iteration)))))
