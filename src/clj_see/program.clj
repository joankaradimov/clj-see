(ns clj-see.program
  (:require [clj-see.util :as util]
            [clj-see.expression :as expression]))

(defprotocol IProgram
  (expression [this])
  (random-path [this]))

(deftype Program [expression
                  ^:unsynchronized-mutable all-paths
                  ^:unsynchronized-mutable func]
  IProgram

  (expression [this] expression)

  (random-path [this]
    (if (nil? all-paths)
      (set! all-paths (util/all-paths expression)))
    (rand-nth all-paths))

  clojure.lang.IFn

  ;; [this & args] does not work. For more info on varargs and protocols:
  ;; http://dev.clojure.org/jira/browse/CLJ-1024
  (invoke [this a1]    (apply this [a1]))
  (invoke [this a1 a2] (apply this [a1 a2]))

  (applyTo [this args]
    (if (nil? func)
      (set! func (eval `(fn [& ~'args] ~expression))))
    (apply func args))

  (equals [this other]
    (= expression (. other expression))))

(defn create-program [expression]
  (Program. expression nil nil))

(defn crossover [program-1 program-2]
  (map create-program
       (expression/crossover (expression program-1)
                             (random-path program-1)
                             (expression program-2)
                             (random-path program-2))))

(defn mutate [program mutate-fn]
  (let [expression (expression program)
        path (random-path program)]
    (-> expression
        (expression/mutate path mutate-fn)
        create-program)))
