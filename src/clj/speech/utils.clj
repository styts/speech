(ns speech.utils)

;; helpers
(defn abs [n] (max n (- n)))

(defn average [numbers]
  (/ (apply + numbers) (count numbers)))

;; handler for new microphone data
(defn calculations
  [data]
  {:total (count data)
   :max (apply max (map abs data))
   :average (int (average (map abs data)))})

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

(defn bytes-to-int
  ([bytes]
   (bytes-to-int bytes 0))
  ([bytes offset]
   (reduce + 0
           (map (fn [i]
                  (let [shift (* (- 4 1 i)
                                 8)]
                    (bit-shift-left (bit-and (nth bytes (+ i offset))
                                             0x000000FF)
                                    shift)))
                (range 0 4)))))
