(ns clj-see.core)

(defn prepend-paths [index paths]
  (if (zero? index)
    [()]
    (for [path paths] (cons index path))))

(defn flatten-1 [list]
  (apply concat list))

(defn all-paths [expression]
  (if (list? expression)
    (->> expression
         (map all-paths)
         (map-indexed prepend-paths)
         flatten-1)
    [()]))
