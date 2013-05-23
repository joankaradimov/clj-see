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

(defn extract-snippet [expression path]
  (if (empty? path)
    expression
    (recur (nth expression (first path)) (rest path))))

(defn replace-snippet [expression path new-snippet]
  (if (empty? path)
    new-snippet
    (map-indexed (fn [index subexpression]
                   (if (= index (first path))
                     (replace-snippet subexpression (rest path) new-snippet)
                     subexpression))
                 expression)))
