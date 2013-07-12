(ns clj-see.core
  (:require [clj-see.population :as population]
            [examples.circle-area]))

(def filename-prefix "circle-area")

(defn -main []
  (let [next-gen population/next-generation
        [initial-population initial-iteration] (population/load-or-create filename-prefix 100)
        persisting-agent (population/create-persisting-agent 0)]
    (loop [population initial-population
           iteration initial-iteration]
      (if (< iteration 120)
        (let [new-population (next-gen population
                                       examples.circle-area/fitness
                                       examples.circle-area/mutate-fn
                                       0.1)]
          (population/dump-async persisting-agent
                                 filename-prefix
                                 new-population
                                 (inc iteration))
          (recur new-population (inc iteration)))
        (do
          (population/pprint population)
          (shutdown-agents))))))
