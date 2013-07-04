(ns examples.circle-area
  (:require clj-see.program))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn abs [x]
  (if (neg? x) (- x) x))

(defn sqr [x]
  (* x x))

(defn fitness [program]
  (let [diff-fn #(abs (- (circle-area %)
                         (clj-see.program/invoke program %)))
        differences (map diff-fn (range 5))]
    (- (apply + (map sqr differences)))))
