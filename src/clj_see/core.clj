(ns clj-see.core
  (:require clj-see.util clj-see.program examples.circle-area))

(defn form-pairs [programs]
  (loop [remaining-exps programs
         accumulator ()]
    (let [first-exp (first remaining-exps)
          second-exp (second remaining-exps)]
      (if (and first-exp second-exp)
        (recur (-> remaining-exps rest rest)
               (conj accumulator [first-exp second-exp]))
        (reverse accumulator)))))

; TODO: fitness can be cached, probably
(defn take-fittest [programs fitness-function count]
  (->> programs
       (sort-by fitness-function >)
       (take count)))

(defn next-generation [programs fitness-fn elitism-factor]
  (let [population-count (count programs)
        old-program-count (* elitism-factor population-count)
        new-program-count (- population-count old-program-count)
        new-programs (->> programs
                          form-pairs
                          (map #(apply clj-see.program/crossover %))
                          clj-see.util/flatten-1)]
    (concat (take-fittest programs fitness-fn old-program-count)
            (take-fittest new-programs fitness-fn new-program-count))))

(def initial-population (map clj-see.program/create-program
                             `[~Math/PI
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
                               (* 1 1)]))

(defn -main []
  (loop [population initial-population
         iteration 0]
    (prn iteration population)
    (if (< iteration 10)
      (recur (next-generation population examples.circle-area/fitness 0.1)
             (inc iteration)))))
