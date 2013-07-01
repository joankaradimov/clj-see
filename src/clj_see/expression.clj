(ns clj-see.expression
  (:require clj-see.util))

(defn random-path [expression]
  (-> expression clj-see.util/all-paths rand-nth))

(defn crossover
  ([expression-1 expression-2]
     (crossover expression-1
                (random-path expression-1)
                expression-2
                (random-path expression-2)))
  ([expression-1 path-1 expression-2 path-2]
     (let [snippet-1 (clj-see.util/list-get-in expression-1 path-1)
           snippet-2 (clj-see.util/list-get-in expression-2 path-2)
           new-expression-1 (clj-see.util/list-assoc-in expression-1
                                                        path-1
                                                        snippet-2)
           new-expression-2 (clj-see.util/list-assoc-in expression-2
                                                        path-2
                                                        snippet-1)]
       [new-expression-1 new-expression-2])))

(defn mutate
  ([expression f]
     (mutate expression (random-path expression) f))
  ([expression path f]
     (let [snippet (clj-see.util/list-get-in expression path)
           mutated-snippet (f snippet)]
       (clj-see.util/list-assoc-in expression path mutated-snippet))))

(defn expression->fn [expression]
  (eval `(fn [~'r] ~expression)))
