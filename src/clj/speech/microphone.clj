(ns speech.microphone
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [chan close! go-loop put! sliding-buffer]]
            [com.stuartsierra.component :as component]
            [speech
             [parameters :as parameters]
             [utils :refer [calculations]]])
  (:import [java.nio ByteBuffer ByteOrder]))

(def audio-channel (chan (sliding-buffer 1)))

(def averages-channel (chan (sliding-buffer 20)))

(def params {:sampleRate parameters/sampling-rate-hz
             :sampleSizeInBits 16
             :n-channels 1
             :signed? true
             :bigEndian? false}) ;; using little endian ordering (defined below)

(def s-array (short-array parameters/samples-per-frame))

(defn- process-buffer! [buffer]
  (let [wb (ByteBuffer/wrap buffer)
        sb (.asShortBuffer (.order wb (ByteOrder/LITTLE_ENDIAN)))]
    (.get sb s-array)
    (map int s-array)))

(defn start-capture []
  "captures some audio from the microphone and puts it into a buffer"

  (def audioformat (new javax.sound.sampled.AudioFormat
                        (:sampleRate params)
                        (:sampleSizeInBits params)
                        (:n-channels params)
                        (:signed? params)
                        (:bigEndian? params)))

  (def buffer (byte-array parameters/bytes-per-frame))

  (def line (javax.sound.sampled.AudioSystem/getTargetDataLine
             audioformat))

  (.open line)
  (.start line)

  (prn (.getFormat line))  ;; print format

  ;; return the line, stored in system. needs to be closed later
  {:line line
   :loop (go-loop []
           (.read line buffer 0 (count buffer))
           (put! audio-channel (process-buffer! buffer))
           (put! averages-channel (-> s-array calculations :average)) ;; FIXME not using the bytes-to-int function as above
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
