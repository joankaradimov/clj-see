(ns examples.circle-area
  (:require clj-see.expression))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn abs [x]
  (if (neg? x) (- x) x))

(defn fitness [expression]
  (let [expression-fn (clj-see.expression/expression->fn expression)
        differences (for [r (range 5)] (abs (- (circle-area r)
                                                (expression-fn r))))]
    (- (apply + (map #(* % %) differences)))))
