(ns clj-see.population
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clj-see.util :as util]
            [clj-see.program :as program]))

(defn create-population [size]
  {:programs (map program/create-program (repeat size 0))
   :size size
   :iteration 0})

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
       (pmap (fn [p] [(fitness-function p) p]))
       (sort-by first >)
       (pmap second)
       (take count)))

(defn next-generation [population fitness-fn mutate-fn elitism-factor]
  (let [population-size (population :size)
        population-iteration (population :iteration)
        old-program-size (* elitism-factor population-size)
        new-program-size (- population-size old-program-size)
        old-programs (population :programs)
        new-programs (->> old-programs
                          shuffle
                          form-pairs
                          (pmap #(apply program/crossover %))
                          util/flatten-1
                          (pmap #(program/mutate % mutate-fn)))
        fittest-old-programs (take-fittest old-programs
                                           fitness-fn
                                           old-program-size)
        fittest-new-programs (take-fittest new-programs
                                           fitness-fn
                                           new-program-size)]
    {:programs (concat fittest-old-programs fittest-new-programs)
     :size population-size
     :iteration (inc population-iteration)}))

(defn pprint
  ([population]
     (pprint population *out*))
  ([population writer]
     (pprint/pprint (mapv program/expression (population :programs)) writer)))

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
    {:programs last-population
     :size (count last-population)
     :iteration last-iteration}))

(defn load-or-create [filename-prefix size]
  (try
    (let [population (load-population filename-prefix)]
      (println "Loaded population from file (iteration"
               (population :iteration)
               ")")
      population)
    (catch Exception e
      (println "Created a new population")
      (create-population size))))

(defn dump-population [filename-prefix population]
  (let [iteration (population :iteration)
        filename (str filename-prefix '- iteration '.txt)]
    (with-open [w (io/writer filename)]
      (pprint population w))))

(defn create-persisting-agent [index]
  (agent index))

(defn dump-async [persisting-agent filename-prefix population]
  (letfn [(dump [index]
            (if (< (.getQueueCount persisting-agent) 3)
              ;; In certain scenarios the generation of new populations can
              ;; overwhelm the dumping of the old ones. This can be avoided
              ;; by skipping the dumping of some populations.
              (dump-population filename-prefix population))
            (inc index))]
    (send-off persisting-agent dump)))
