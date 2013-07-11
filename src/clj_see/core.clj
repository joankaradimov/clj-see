(ns clj-see.core
  (:require [clj-see.population :as population]
            [examples.circle-area]))

(def filename "circle-area.txt")

(defn -main []
  (let [next-gen population/next-generation
        initial-population (population/load-or-create filename 100)
        persisting-agent (population/create-persisting-agent 0)]
    (loop [population initial-population
           iteration 0]
      (if (< iteration 120)
        (let [new-population (next-gen population
                                       examples.circle-area/fitness
                                       examples.circle-area/mutate-fn
                                       0.1)]
          (population/dump-async persisting-agent filename new-population)
          (recur new-population (inc iteration)))
        (do
          (population/pprint population)
          (shutdown-agents))))))
