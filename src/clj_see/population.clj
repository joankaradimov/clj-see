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
                          shuffle
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

(defn pprint
  ([population]
     (pprint population *out*))
  ([population writer]
     (pprint/pprint (mapv program/expression population) writer)))

(defn load-population [filename-prefix]
  (let [pattern (->> [filename-prefix '- "([0-9]+)" '.txt]
                     (apply str)
                     re-pattern)
        get-info #(let [[filename i] (re-matches pattern (.getName %))]
                    {:file %
                     :matched-filename filename
                     :matched-iteration (if (nil? i) nil (read-string i))})
        files-info (->> "."
                        clojure.java.io/file
                        file-seq
                        (map get-info)
                        (filter :matched-filename)
                        (sort-by :matched-iteration))
        last-iteration (-> files-info
                           last
                           :matched-iteration) ; TODO: resole NPE
        last-filename (-> files-info
                           last
                           :file ; TODO: resole NPE
                           .getAbsolutePath)
        last-population (->> last-filename
                             slurp
                             read-string
                             (map program/create-program))]
    [last-population last-iteration]))

(defn load-or-create [filename-prefix size]
  (try
    (let [[population iteration] (load-population filename-prefix)]
      (println "Loaded population from file (iteration" iteration ")")
      [population iteration])
    (catch Exception e
      (println "Created a new population")
      [(create-population size) 0])))

(defn dump-population [filename-prefix population iteration]
  (let [filename (str filename-prefix '- iteration '.txt)]
    (with-open [w (io/writer filename)]
      (pprint population w))))

(defn create-persisting-agent [index]
  (agent index))

(defn dump-async [persisting-agent filename-prefix population iteration]
  (letfn [(dump [index]
            (if (< (.getQueueCount persisting-agent) 3)
              ;; In certain scenarios the generation of new populations can
              ;; overwhelm the dumping of the old ones. This can be avoided
              ;; by skipping the dumping of some populations.
              (dump-population filename-prefix population iteration))
            (inc index))]
    (send-off persisting-agent dump)))
