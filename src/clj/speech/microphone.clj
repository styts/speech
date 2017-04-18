(ns speech.microphone
  (:require [environ.core :refer [env]]))

;; helpers
(defn abs [n] (max n (- n)))

(defn average [numbers]
  (/ (apply + numbers) (count numbers)))

;; handler for new microphone data
(defn print-data
  ;; (println (reduce + buf)
  [data]
  ["total:"
   (count data)
   "max:"
   (apply max (map abs data))
   "average:"
   (int (average (map abs data)))
   ;; (take 20 data)
])

;; main thread for getting new microphone data
(defn capture
  "captures some audio from the microphone"
  ([] (capture print-data))
  ([seconds] (capture print-data seconds))
  ([callback seconds]

   ;; setup the device
   (def audioformat (new javax.sound.sampled.AudioFormat 8000 16 1 true false))
   (def line (javax.sound.sampled.AudioSystem/getTargetDataLine audioformat))

   (.open line)
   (.start line)

   (println (.getFormat line))

   (def buffer-size (Integer. (env :buffer-size "800")))

   (def buffer (byte-array buffer-size))

   (print (repeatedly
           (fn []
             (.read line buffer 0 buffer-size)
             (println (callback buffer)))))

   (.close line)))

(comment
  (capture)
  (+ 1 2)

  (env)
  (read-string "400")
  (doc-find "average")
  (average [2 1])
  (apply max [2 1])
  (print-data buffer)
  (def seconds 4))
