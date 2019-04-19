(ns clj-see.program
  (:require [clj-see.util :as util]
            [clj-see.expression :as expression]))

(defprotocol IProgram
  (expression [this])
  (random-path [this]))

(deftype Program [expression
                  ^:unsynchronized-mutable all-paths-cache
                  ^:unsynchronized-mutable func-cache]
  IProgram

  (expression [this] expression)

  (random-path [this]
    (if (nil? all-paths-cache)
      (set! all-paths-cache (util/all-paths expression)))
    (rand-nth all-paths-cache))

  clojure.lang.IFn

  ;; [this & args] does not work. For more info on varargs and protocols:
  ;; http://dev.clojure.org/jira/browse/CLJ-1024
  ;; Implementing `invoke` with up to 6 arguments should be enough
  (invoke [this]                   (apply this []))
  (invoke [this a1]                (apply this [a1]))
  (invoke [this a1 a2]             (apply this [a1 a2]))
  (invoke [this a1 a2 a3]          (apply this [a1 a2 a3]))
  (invoke [this a1 a2 a3 a4]       (apply this [a1 a2 a3 a4]))
  (invoke [this a1 a2 a3 a4 a5]    (apply this [a1 a2 a3 a4 a5]))
  (invoke [this a1 a2 a3 a4 a5 a6] (apply this [a1 a2 a3 a4 a5 a6]))

  (applyTo [this args]
    (if (nil? func-cache)
      (set! func-cache (eval `(fn [& ~'args] ~expression))))
    (apply func-cache args))

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
