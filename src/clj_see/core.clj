(ns clj-see.core)

(defn prepend-paths [index paths]
  (for [path paths] (cons index path)))

(defn flatten-1 [list]
  (apply concat list))

(defn all-paths [expression]
  (if (list? expression)
    (->> expression
         rest
         (map all-paths)
         (map prepend-paths (iterate inc 1))
         flatten-1
         (cons ()))
    [()]))

(defn extract-snippet [expression path]
  (if (empty? path)
    expression
    (recur (nth expression (first path)) (rest path))))

(defn replace-snippet [expression path new-snippet]
  (let [current-index (first path)
        remaining-indexes (rest path)
        replace-flat (fn [index subexpression]
                       (if (= index current-index)
                         (replace-snippet subexpression
                                          remaining-indexes
                                          new-snippet)
                         subexpression))]
    (if (empty? path)
      new-snippet
      (map-indexed replace-flat expression))))

(defn random-path [expression]
  (-> expression all-paths rand-nth))

(defn crossover [expression-1 path-1 expression-2 path-2]
  (let [snippet-1 (extract-snippet expression-1 path-1)
        snippet-2 (extract-snippet expression-2 path-2)
        new-expression-1 (replace-snippet expression-1 path-1 snippet-2)
        new-expression-2 (replace-snippet expression-2 path-2 snippet-1)]
    [new-expression-1 new-expression-2]))

(defn mutate [expression path f]
  (let [snippet (extract-snippet expression path)
        mutated-snippet (f snippet)]
    (replace-snippet expression path mutated-snippet)))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn abs [x]
  (if (neg? x) (- x) x))

(defn circle-area-fitness [expression]
  (let [expression-fn (eval `(fn [~'r] ~expression))
        differences (for [r (range 10)] (abs (- (circle-area r)
                                                (expression-fn r))))]
    (- (apply + (map #(* % %) differences)))))

; TODO: This can be a tail recursion
(defn form-pairs [expressions]
  (let [first-expression (first expressions)
        second-expression (second expressions)
        remaining-expressions (-> expressions rest rest)]
    (if (and first-expression second-expression)
      (cons [first-expression second-expression]
            (form-pairs remaining-expressions)))))

; TODO: fitness can be cached, probably
(defn take-fittest [expressions fitness-function count]
  (->> expressions
       (sort-by fitness-function >)
       (take count)))

(defn next-generation [expressions elitism-factor]
  (let [population-count (count expressions)
        old-expression-count (* elitism-factor population-count)
        new-expression-count (- population-count old-expression-count)
        new-expressions (flatten-1 (for [[exp-1 exp-2] (form-pairs expressions)]
                                     (crossover exp-1
                                                (random-path exp-1)
                                                exp-2
                                                (random-path exp-2))))]
    (concat (take-fittest expressions
                          circle-area-fitness
                          old-expression-count)
            (take-fittest new-expressions
                          circle-area-fitness
                          new-expression-count))))
