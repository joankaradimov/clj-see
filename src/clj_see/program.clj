(ns clj-see.program
  (:require clj-see.util clj-see.expression))

(defprotocol AProtocol
  (expression [this])
  (random-path [this])
  (invoke [this r]))

(deftype Program [expression
                  ^:unsynchronized-mutable all-paths
                  ^:unsynchronized-mutable func]
  AProtocol

  (expression [this] expression)

  (random-path [this]
    (if (nil? all-paths)
      (set! all-paths (clj-see.util/all-paths expression)))
    (rand-nth all-paths))

  ; TODO: use `args` instead of `r`
  (invoke [this r]
    (if (nil? func)
      (set! func (eval `(fn [~'r] ~expression))))
    (func r))

  (equals [this other]
    (= expression (. other expression))))

(defn create-program [expression]
  (Program. expression nil nil))

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
