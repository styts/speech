(ns speech.microphone
  (:require [clojure.core.async :refer [chan close! go-loop put! sliding-buffer]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [speech.utils :refer [abs calculations]]))

(def audio-channel (chan (sliding-buffer 20)))
(def averages-channel (chan (sliding-buffer 20)))

;; main thread for getting new microphone data
(defn start-capture []
  "captures some audio from the microphone and puts it into a buffer"

  ;; globals
  (def audioformat (new javax.sound.sampled.AudioFormat 8000 16 1 true false))
  (def buffer-size (Integer. (env :buffer-size "20")))
  (def buffer (byte-array buffer-size))

  (def line (javax.sound.sampled.AudioSystem/getTargetDataLine audioformat))

  (.open line)
  (.start line)

  ;; print format
  (println (.getFormat line))
  (println "buffer size. desired:" buffer-size "real:" (.getBufferSize line))

  ;; return the line, stored in system. needs to be closed later
  {:line line
   :loop (go-loop []
           (.read line buffer 0 buffer-size)
           (put! audio-channel (map abs buffer))
           (put! averages-channel (-> buffer calculations :average))
           (recur))})

(defrecord Capture []
  component/Lifecycle
  (start [this]
    (assoc this :microphone (start-capture)))
  (stop [this]
    (let [{:keys [loop line]} (:microphone this)]
      (.close line) ;; close the recording line
      (close! loop) ;; terminate the go-loop channel
      (dissoc this :microphone)
      (println "Stopped microphone recording")
      this)))

(defn create-system []
  (Capture.))
