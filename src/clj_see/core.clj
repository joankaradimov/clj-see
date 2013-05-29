(ns clj-see.core)

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

(defn crossover
  ([expression-1 expression-2]
     (crossover expression-1
                (random-path expression-1)
                expression-2
                (random-path expression-2)))
  ([expression-1 path-1 expression-2 path-2]
     (let [snippet-1 (extract-snippet expression-1 path-1)
           snippet-2 (extract-snippet expression-2 path-2)
           new-expression-1 (replace-snippet expression-1 path-1 snippet-2)
           new-expression-2 (replace-snippet expression-2 path-2 snippet-1)]
       [new-expression-1 new-expression-2])))

(defn mutate
  ([expression f]
     (mutate expression (random-path expression) f))
  ([expression path f]
     (let [snippet (extract-snippet expression path)
           mutated-snippet (f snippet)]
       (replace-snippet expression path mutated-snippet))))

(defn circle-area [radius]
  (* radius radius Math/PI))

(defn abs [x]
  (if (neg? x) (- x) x))

(defn circle-area-fitness [expression]
  (let [expression-fn (eval `(fn [~'r] ~expression))
        differences (for [r (range 5)] (abs (- (circle-area r)
                                                (expression-fn r))))]
    (- (apply + (map #(* % %) differences)))))

(defn form-pairs [expressions]
  (loop [remaining-exps expressions
         accumulator ()]
    (let [first-exp (first remaining-exps)
          second-exp (second remaining-exps)]
      (if (and first-exp second-exp)
        (recur (-> remaining-exps rest rest)
               (conj accumulator [first-exp second-exp]))
        (reverse accumulator)))))

; TODO: fitness can be cached, probably
(defn take-fittest [expressions fitness-function count]
  (->> expressions
       (sort-by fitness-function >)
       (take count)))

(defn next-generation [expressions elitism-factor]
  (let [population-count (count expressions)
        old-expression-count (* elitism-factor population-count)
        new-expression-count (- population-count old-expression-count)
        new-expressions (->> expressions
                             form-pairs
                             (map #(apply crossover %))
                             flatten-1)]
    (concat (take-fittest expressions
                          circle-area-fitness
                          old-expression-count)
            (take-fittest new-expressions
                          circle-area-fitness
                          new-expression-count))))

(def initial-population `[~Math/PI
                          ~'r
                          (+ 0 0)
                          (* 1 1)
                          (- 0 0)
                          ~Math/PI
                          (+ 0 0)
                          (* 1 1)
                          (+ 0 0)
                          (* 1 1)
                          ~Math/PI
                          ~'r
                          (+ 2 0)
                          (* 2 1)
                          (- 1 1)
                          ~Math/PI
                          (* 1 0)
                          (* 1 1)
                          (* 0 0)
                          (* 1 1)])

(defn -main []
  (loop [population initial-population
         iteration 0]
    (prn iteration population)
    (if (< iteration 10)
      (recur (next-generation population 0.1) (inc iteration)))))
