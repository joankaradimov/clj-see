(ns clj-see.expression
  (:require clj-see.util))

(defn crossover [expression-1 path-1 expression-2 path-2]
  (let [snippet-1 (clj-see.util/list-get-in expression-1 path-1)
        snippet-2 (clj-see.util/list-get-in expression-2 path-2)
        new-expression-1 (clj-see.util/list-assoc-in expression-1
                                                     path-1
                                                     snippet-2)
        new-expression-2 (clj-see.util/list-assoc-in expression-2
                                                     path-2
                                                     snippet-1)]
    [new-expression-1 new-expression-2]))

(defn mutate [expression path f]
  (let [snippet (clj-see.util/list-get-in expression path)
        mutated-snippet (f snippet)]
    (clj-see.util/list-assoc-in expression path mutated-snippet)))
