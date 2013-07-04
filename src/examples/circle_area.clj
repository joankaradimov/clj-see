(ns examples.circle-area
  (:require clj-see.program))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn abs [x]
  (if (neg? x) (- x) x))

(defn sqr [x]
  (* x x))

(defn fitness [program]
  (let [program-fn (clj-see.program/program->fn program)
        differences (for [r (range 5)] (abs (- (circle-area r)
                                               (program-fn r))))]
    (- (apply + (map sqr differences)))))
