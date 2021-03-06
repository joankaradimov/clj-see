(ns clj-see.util)

(defn prepend-paths [index paths]
  (for [path paths] (conj path index)))

(defn flatten-1 [list]
  (apply concat list))

(defn all-paths [expression]
  (if (seq? expression)
    (->> expression
         rest
         (map all-paths)
         (map prepend-paths (iterate inc 1))
         flatten-1
         (cons ()))
    [()]))

(defn list-get-in [expression path]
  (if (empty? path)
    expression
    (recur (nth expression (first path)) (rest path))))

(defn list-assoc-in [expression path new-snippet]
  (let [current-index (first path)
        remaining-indexes (rest path)
        replace-flat (fn [index subexpression]
                       (if (= index current-index)
                         (list-assoc-in subexpression
                                        remaining-indexes
                                        new-snippet)
                         subexpression))]
    (if (empty? path)
      new-snippet
      (map-indexed replace-flat expression))))
