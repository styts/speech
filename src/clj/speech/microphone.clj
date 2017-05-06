(ns speech.microphone
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [chan close! go-loop put! sliding-buffer]]
            [com.stuartsierra.component :as component]
            [speech
             [parameters :as parameters]
             [utils :refer [abs calculations]]]))

(def audio-channel (chan (sliding-buffer 1)))

(def averages-channel (chan (sliding-buffer 20)))

(defn start-capture []
  "captures some audio from the microphone and puts it into a buffer"

  ;; globals
  (def audioformat (new javax.sound.sampled.AudioFormat
                        parameters/sampling-rate-hz 16 1 true false))
  (def buffer-size parameters/samples-per-frame)
  (def buffer (byte-array buffer-size))

  (def line (javax.sound.sampled.AudioSystem/getTargetDataLine
             audioformat))

  (.open line)
  (.start line)

  ;; print format
  (prn (.getFormat line))
  (prn "buffer size. desired:" buffer-size)
  (prn "line buffer size:" (.getBufferSize line))

  ;; return the line, stored in system. needs to be closed later
  {:line line
   :loop (go-loop []
           (.read line buffer 0 (count buffer))
           (put! audio-channel (map int buffer))
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
      (prn "Stopped microphone recording")
      this)))

(defn create-system []
  (Capture.))
