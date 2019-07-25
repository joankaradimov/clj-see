(ns clj-see.population
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [me.raynes.fs :as fs]
            [clj-see.util :as util]
            [clj-see.program :as program]))

(defn nan? [number]
  (Double/isNaN number))

(defn create-population
  ([size]
     (create-population (->> 0 (repeat size) (map program/create-program)) 0))
  ([programs iteration]
     {:programs programs
      :size (count programs)
      :iteration iteration}))

(defn form-pairs
  ([programs]
     (form-pairs programs ()))
  ([programs result-accumulator]
     (let [first-program (first programs)
           second-program (second programs)]
       (if (and first-program second-program)
         (recur (-> programs rest rest)
                (conj result-accumulator [first-program second-program]))
         (reverse result-accumulator)))))

(defn take-fittest [programs fitness-function count]
  (->> programs
       (pmap (fn [p] [(fitness-function p) p]))
       (remove (fn [program-with-fitness] (nan? (first program-with-fitness))))
       (sort-by first >)
       (pmap second)
       (take count)))

(defn next-generation [population fitness-fn mutate-fn elitism-factor]
  (let [population-size (population :size)
        population-iteration (population :iteration)
        old-programs (population :programs)
        old-programs-set (set old-programs)
        new-programs-size (* population-size (- 1 elitism-factor))
        new-programs (->> old-programs
                          shuffle
                          form-pairs
                          (pmap #(apply program/crossover %))
                          util/flatten-1
                          (pmap #(program/mutate % mutate-fn))
                          (remove #(contains? old-programs-set %)))
        fittest-new (take-fittest new-programs fitness-fn new-programs-size)
        old-programs-size (- population-size (count fittest-new))
        fittest-old (take-fittest old-programs fitness-fn old-programs-size)]
    (create-population (concat fittest-old fittest-new)
                       (inc population-iteration))))

(defn pprint
  ([population]
     (pprint population *out*))
  ([population writer]
     (pprint/pprint (mapv program/expression (population :programs)) writer)))

(defn create-population-filename-pattern [filename-prefix]
  (->> [filename-prefix '- "([0-9]+)" '.txt]
       (apply str)
       re-pattern))

(defn file->file-info [file filename-prefix]
  (let [filename-pattern (create-population-filename-pattern filename-prefix)
        filename (.getName file)
        [matched-filename matched-i] (re-matches filename-pattern filename)]
    {:absolute-path (.getAbsolutePath file)
     :matched-filename matched-filename
     :matched-iteration (if matched-i (read-string matched-i))}))

(defn load-population [filename-prefix]
  (let [files-info (->> "."
                        clojure.java.io/file
                        file-seq
                        (map #(file->file-info % filename-prefix))
                        (filter :matched-filename))
        last-file-info (if (seq files-info)
                         (apply max-key :matched-iteration files-info))]
    (if last-file-info
      (let [last-iteration (last-file-info :matched-iteration)
            last-population (->> last-file-info
                                 :absolute-path
                                 slurp
                                 read-string
                                 (map program/create-program))]
        (create-population last-population last-iteration)))))

(defn load-or-create [filename-prefix size]
  (if-let [population (load-population filename-prefix)]
    (do (println "Loaded population from file - iteration"
                 (population :iteration))
        population)
    (do (println "Created a new population")
        (create-population size))))

(defn dump-population [filename-prefix population]
  (let [iteration (population :iteration)
        filename (str filename-prefix '- iteration '.txt)]
    (fs/mkdirs (fs/parent filename))
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
