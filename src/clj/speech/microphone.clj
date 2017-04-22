(ns speech.microphone
  (:require [cheshire.core :refer [generate-string]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [clojure.core.async :refer [go-loop]]
            [speech.web :refer [send-data-to-ws]]))

;; globals
(def audioformat (new javax.sound.sampled.AudioFormat 8000 16 1 true false))
(def buffer-size (Integer. (env :buffer-size "800")))
(def buffer (byte-array buffer-size))

;; helpers
(defn abs [n] (max n (- n)))

(defn average [numbers]
  (/ (apply + numbers) (count numbers)))

;; handler for new microphone data
(defn calculations
  ;; (println (reduce + buf)
  [data]
  ["total:" (count data)
   "max:" (apply max (map abs data))
   "average:" (int (average (map abs data)))])

;; main thread for getting new microphone data
(defn start-capture []
  "captures some audio from the microphone"
  (def line (javax.sound.sampled.AudioSystem/getTargetDataLine audioformat))

  (.open line)
  (.start line)

  ;; print format
  (println (.getFormat line))
  (println "buffer size. desired:" buffer-size "real:" (.getBufferSize line))

  (go-loop []
      (.read line buffer 0 buffer-size)
      (-> buffer
          calculations
          generate-string
          send-data-to-ws)
      (recur))

  ;; return the line, stored in system. needs to be closed later
  line)

(defrecord Capture []
  component/Lifecycle
  (start [this]
    (assoc this :microphone (start-capture)))
  (stop [this]
    (.close (:microphone this)) ;; close the recording line
    (dissoc this :microphone)
    (println "Stopped microphone recording")
    this))

(defn create-system []
  (Capture.))
