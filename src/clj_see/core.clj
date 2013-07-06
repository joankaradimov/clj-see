(ns clj-see.core
  (:require [clj-see.population :as population]
            [clj-see.program :as program]
            [examples.circle-area]))

(defn -main []
  (loop [population (population/create-population 30)
         iteration 0]
    (if (< iteration 80)
      (recur (population/next-generation population
                                         examples.circle-area/fitness
                                         0.1)
             (inc iteration))
      (prn iteration (map program/expression population)))))
