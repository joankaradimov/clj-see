(ns clj-see.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clj-see.population :as population]
            [clj-see.program :as program]
            [examples.circle-area]))

(def filename "circle-area.txt")

(defn create-persisting-agent [index]
  (agent index))

(defn load-population [filename]
  (-> filename
      slurp
      population/deserialize))

(defn dump-population [filename population]
  (with-open [w (io/writer filename)]
    (pprint (mapv program/expression population) w)))

(defn dump-population-async [persisting-agent filename population]
  (letfn [(dump [index]
            (dump-population filename population)
            (inc index))]
    (send-off persisting-agent dump)))

(defn load-or-create-population [filename]
  (try
    (let [population (load-population filename)]
      (println "Loaded population from file")
      population)
    (catch Exception e
      (println "Created a new population")
      (population/create-population 30))))

(defn -main []
  (let [next-gen population/next-generation
        initial-population (load-or-create-population filename)
        persisting-agent (create-persisting-agent 0)]
    (loop [population initial-population
           iteration 0]
      (if (< iteration 80)
        (let [new-population (next-gen population
                                       examples.circle-area/fitness
                                       examples.circle-area/mutate-fn
                                       0.1)]
          (dump-population-async persisting-agent filename new-population)
          (recur new-population (inc iteration)))
        (population/pprint population)))))
