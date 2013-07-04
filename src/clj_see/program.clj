(ns clj-see.program
  (:require clj-see.util clj-see.expression))

(defn create-program [expression]
  {:expression expression})

(defn expression [program]
  (program :expression))

(defn random-path [program]
  (-> program expression clj-see.util/all-paths rand-nth))

(defn program->fn [program]
  (eval `(fn [~'r] ~(program :expression))))

(defn crossover [program-1 program-2]
  (map create-program
       (clj-see.expression/crossover (expression program-1)
                                     (random-path program-1)
                                     (expression program-2)
                                     (random-path program-2))))

(defn mutate [program mutate-fn]
  (let [expression (expression program)
        path (random-path program)]
    (-> expression
        (clj-see.expression/mutate path mutate-fn)
        create-program)))
