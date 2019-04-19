(ns clj-see.core
  (:require [clj-see.population :as population]
            [examples.circle-area]))

(def filename-prefix "circle-area")

(defn -main []
  (let [next-gen population/next-generation
        initial-population (population/load-or-create filename-prefix 100)
        persisting-agent (population/create-persisting-agent 0)]
    (loop [population initial-population]
      (if (< (population :iteration) 240)
        (let [new-population (next-gen population
                                       examples.circle-area/fitness
                                       examples.circle-area/mutate-fn
                                       0.1)]
          (population/dump-async persisting-agent
                                 filename-prefix
                                 new-population)
          (recur new-population))
        (do
          (population/pprint population)
          (shutdown-agents))))))
