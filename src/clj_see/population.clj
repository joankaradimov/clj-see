(ns clj-see.population
  (:require [clj-see.util :as util]
            [clj-see.program :as program]))

(def initial-population (map program/create-program
                             (repeat 30 0)))

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
                          (map #(apply program/crossover %))
                          util/flatten-1
                          (map program/mutate))]
    (concat (take-fittest programs fitness-fn old-program-count)
            (take-fittest new-programs fitness-fn new-program-count))))