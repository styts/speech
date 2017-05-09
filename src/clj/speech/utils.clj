(ns speech.utils
  (:require [clojure.core
             [async :as a :refer [<! <!! go-loop]]
             [matrix :refer [to-nested-vectors]]]
            [speech
             [fft :refer [get-fft]]
             [web :refer [ws-send]]
             [windowing :refer [hammer]]])
  (:import java.lang.Math))

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

(defn debug-channel
  "Prints what the channel receives"
  [label channel]
  (go-loop []
    (let [x (<! channel)]
      (println label x)
      (recur))))
