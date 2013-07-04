(ns clj-see.program
  (:require clj-see.util clj-see.expression))

(defn create-program [expression]
  {:expression expression})

(defn all-paths [program]
  (-> program :expression clj-see.util/all-paths))

(defn expression [program]
  (program :expression))

(defn program->fn [program]
  (clj-see.expression/expression->fn (program :expression)))

(defn crossover [program-1 program-2]
  (map create-program
       (clj-see.expression/crossover (expression program-1)
                                     (expression program-2))))

(defn mutate [program mutate-fn]
  (create-program (clj-see.expression/mutate (expression program) mutate-fn)))
