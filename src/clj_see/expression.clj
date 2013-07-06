(ns clj-see.expression
  (:require [clj-see.util :as util]))

(defn crossover [expression-1 path-1 expression-2 path-2]
  (let [snippet-1 (util/list-get-in expression-1 path-1)
        snippet-2 (util/list-get-in expression-2 path-2)
        new-expression-1 (util/list-assoc-in expression-1
                                             path-1
                                             snippet-2)
        new-expression-2 (util/list-assoc-in expression-2
                                             path-2
                                             snippet-1)]
    [new-expression-1 new-expression-2]))

(defn mutate [expression path f]
  (let [snippet (util/list-get-in expression path)
        mutated-snippet (f snippet)]
    (util/list-assoc-in expression path mutated-snippet)))
