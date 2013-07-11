(ns clj-see.population
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clj-see.util :as util]
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
       (pmap (fn [p] [(fitness-function p) p]))
       (sort-by first >)
       (pmap second)
       (take count)))

(defn next-generation [population fitness-fn mutate-fn elitism-factor]
  (let [population-count (count population)
        old-program-count (* elitism-factor population-count)
        new-program-count (- population-count old-program-count)
        new-programs (->> population
                          form-pairs
                          (pmap #(apply program/crossover %))
                          util/flatten-1
                          (pmap #(program/mutate % mutate-fn)))
        fittest-old-programs (take-fittest population
                                           fitness-fn
                                           old-program-count)
        fittest-new-programs (take-fittest new-programs
                                           fitness-fn
                                           new-program-count)]
    (concat fittest-old-programs fittest-new-programs)))

(defn pprint [population]
  (->> population
       (mapv program/expression)
       pprint/pprint))

(defn load-population [filename]
  (->> filename
       slurp
       read-string
       (map program/create-program)))

(defn load-or-create [filename size]
  (try
    (let [population (load-population filename)]
      (println "Loaded population from file")
      population)
    (catch Exception e
      (println "Created a new population")
      (create-population size))))

(defn dump-population [filename population]
  (with-open [w (io/writer filename)]
    (pprint/pprint (mapv program/expression population) w)))

(defn create-persisting-agent [index]
  (agent index))

(defn dump-async [persisting-agent filename population]
  (letfn [(dump [index]
            (if (< (.getQueueCount persisting-agent) 3)
              ;; In certain scenarios the generation of new populations can
              ;; overwhelm the dumping of the old ones. This can be avoided
              ;; by skipping the dumping of some populations.
              (dump-population filename population))
            (inc index))]
    (send-off persisting-agent dump)))
