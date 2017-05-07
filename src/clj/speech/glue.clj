(ns speech.glue
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [<! close! go-loop]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [get-fft]]
             [microphone :refer [averages-channel]]
             [web :refer [add-data-to-buffer-and-maybe-send ws-send]]]))

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

;; This component starts the go loops that read/write from/to the data channels
(defrecord Glue []
  component/Lifecycle
  (start [this]
    (prn "Started Glue go-loops")
    (assoc this :glue {:go-avg (go-averages)
                       :go-fft (go-fft)}))
  (stop [this]
    (let [{:keys [go-avg go-fft]} (:glue this)]
      (prn this (keys this) (keys (:glue this)))
      (close! go-avg)
      (close! go-fft)
      (dissoc this :glue)
      (prn "Stopped Glue go-loops")
      this)))

(defn create-glue []
  (Glue.))
