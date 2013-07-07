(ns clj-see.core
  (:require [clj-see.population :as population]
            [clj-see.program :as program]
            [examples.circle-area]))

(def population-filename "circle-area.txt")

(defn load-population [filename]
  (-> filename
      slurp
      population/deserialize))

(defn dump-population [filename population]
  (->> population
       population/serialize
       (spit filename)))

(defn load-or-create-population [filename]
  (try
    (let [population (load-population filename)]
      (println "Loaded population from file")
      population)
    (catch Exception e
      (println "Created a new population")
      (population/create-population 30))))

(defn -main []
  (loop [population (load-or-create-population population-filename)
         iteration 0]
    (if (< iteration 80)
      (let [next-gen population/next-generation
            new-population (next-gen population
                                     examples.circle-area/fitness
                                     examples.circle-area/mutate-fn
                                     0.1)]
        (dump-population population-filename new-population)
        (recur new-population (inc iteration)))
      (-> population population/serialize print))))
