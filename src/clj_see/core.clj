(ns clj-see.core
  (:require [clj-see.population :as population]
            [clj-see.program :as program]
            [examples.circle-area]))

(defn -main []
  (loop [population (population/create-population 30)
         iteration 0]
    (if (< iteration 80)
      (let [next-gen population/next-generation
            new-population (next-gen population
                                     examples.circle-area/fitness
                                     0.1)]
        (recur new-population (inc iteration)))
      (-> population population/serialize print))))
