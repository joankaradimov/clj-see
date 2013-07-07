(ns clj-see.program
  (:require [clojure.math.numeric-tower :refer [expt]]
            [clj-see.util :as util]
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
  (invoke [this a1]
    (if (nil? func)
      (set! func (eval `(fn [& ~'args] ~expression))))
    (func a1))
  (invoke [this a1 a2]
    (if (nil? func)
      (set! func (eval `(fn [& ~'args] ~expression))))
    (func a1 a2))

  ; TODO: implement applyTo

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

(defmacro sum-args [x y] '(apply + args))

(def non-terminal-mutations
  [(fn [_] (rand-nth (rest _)))
   (fn [_] (rand-nth (rest _)))
   (fn [_] `(+ ~_ 0))
   (fn [_] `(* ~_ 1))])

(def terminal-mutations
  [(fn [_] (expt 2 (/ 1 (rand))))
   (fn [_] (rand))
   (fn [_] `(sum-args 0 0))
   (fn [_] `(+ ~_ 0))
   (fn [_] `(* ~_ 1))])

(defn mutate-fn [expression]
  (let [mutations (if (seq? expression)
                    non-terminal-mutations
                    terminal-mutations)]
    ((rand-nth mutations) expression)))

(defn mutate [program]
  (let [expression (expression program)
        path (random-path program)]
    (-> expression
        (expression/mutate path mutate-fn)
        create-program)))
