(ns clj-see.population
  (:require [clj-see.util :as util]
            [clj-see.program :as program]))

(defn create-population [size]
  (map program/create-program
       (repeat size 0)))

(defn form-pairs [population]
  (loop [remaining-exps population
         accumulator ()]
    (let [first-exp (first remaining-exps)
          second-exp (second remaining-exps)]
      (if (and first-exp second-exp)
        (recur (-> remaining-exps rest rest)
               (conj accumulator [first-exp second-exp]))
        (reverse accumulator)))))

; TODO: fitness can be cached, probably
(defn take-fittest [population fitness-function count]
  (->> population
       (sort-by fitness-function >)
       (take count)))

(defn next-generation [population fitness-fn elitism-factor]
  (let [population-count (count population)
        old-program-count (* elitism-factor population-count)
        new-program-count (- population-count old-program-count)
        new-programs (->> population
                          form-pairs
                          (map #(apply program/crossover %))
                          util/flatten-1
                          (map program/mutate))]
    (concat (take-fittest population fitness-fn old-program-count)
            (take-fittest new-programs fitness-fn new-program-count))))
