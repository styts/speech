(ns speech.utils)

;; helpers
(defn abs [n] (max n (- n)))

(defn average [numbers]
  (/ (apply + numbers) (count numbers)))

;; handler for new microphone data
(defn calculations
  ;; (println (reduce + buf)
  [data]
  {:total (count data)
   :max (apply max (map abs data))
   :average (int (average (map abs data)))}
  ;; (take 100000 data)
)

(defn split-by
  "Create from sequence (l) sequence of sequences with specified number of elemenets (c)
   Example:
     (split-by 2 [1 2 3 4 5 6 7])
     => '((1 2) (3 4) (5 6) (7))"
  [c l]
  (if (seq l)
    (cons (take c l) (split-by c (drop c l)))))

(comment
  (map average (split-by 10 x))
  (average [0 0 0]))
