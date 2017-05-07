(ns speech.glue
  (:require [cfft.core :refer [fft]]
            [clojure.core :refer [prn]]
            [clojure.core.async :refer [<! <!! go-loop timeout]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [clean-fft get-fft]]
             [microphone :refer [audio-channel averages-channel]]
             [web :refer [add-data-to-buffer-and-maybe-send ws-send]]]
            [system.repl :refer [start stop]]))

(defn go-averages []
  (go-loop []
    (-> averages-channel
        <!
        add-data-to-buffer-and-maybe-send)
    (recur)))

(defn go-fft []
  (go-loop []
    (ws-send {:fft (get-fft)})
    (recur)))

(defn send-frame
  ([] (send-frame 500))
  ([timeout-ms]
   (let [t (timeout timeout-ms)]
     (<!! t)
     (let [data (<!! audio-channel)
           fftd (fft data)
           p    (clean-fft fftd)]
       (ws-send {:frame data :power p})))))

;; This component starts the go loops that read/write from/to the data channels
(defrecord Glue []
  component/Lifecycle
  (start [this]
    (prn "Started Glue go-loops")
    (assoc this :glue {:go-avg (go-averages)
                       :go-fft (go-fft)}))
  (stop [this]
    (let [{:keys [go-avg go-fft]} (:glue this)]
      ;; (prn this (keys this) (keys (:glue this)))
      ;; Throws error
      ;; (close! go-avg)
      ;; (close! go-fft)
      (dissoc this :glue)
      (prn "Stopped Glue go-loops")
      this)))

(defn create-glue []
  (Glue.))

