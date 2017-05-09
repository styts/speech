(ns speech.glue
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [chan go-loop <!]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [get-fft]]
             [microphone :refer [audio-channel]]
             [web :refer [ws-send]]
             [windowing :refer [splitter]]]
            [system.repl :refer [start stop]]))

(def window-channel (chan))

#_(defn go-averages []
  (go-loop []
    (-> averages-channel
        <!
        add-data-to-buffer-and-maybe-send)
    (recur)))
;; (def pi (double (/ 22 7)))
;; (with-precision 3 (double (/ 22 7)))
(defn go-fft! []
  (go-loop []
    (let [x (get-fft (<! window-channel))]
      (when x
        (ws-send {:fft (map float x)})
        (recur)))))

(defn go-window! []
  (splitter audio-channel window-channel 2))

#_(defn send-frame
  ([] (send-frame 500))
  ([timeout-ms]
   (let [t (timeout timeout-ms)]
     (<!! t)
     (let [data (<!! audio-channel)
           fftd (j-fft data)
           p    (clean-fft fftd)]
       (ws-send {:frame data :power p})))))

;; This component starts the go loops that read/write from/to the data channels
(defrecord Glue []
  component/Lifecycle
  (start [this]
    (prn "Started Glue go-loops")
    (assoc this :glue {;:go-avg (go-averages)
                       :go-window (go-window!)
                       :go-fft (go-fft!)}))
  (stop [this]
    (let [{:keys [go-avg go-fft]} (:glue this)]
      ;; TODO make go loops stoppable
      ;; (prn this (keys this) (keys (:glue this)))
      ;; Throws error
      ;; (close! go-avg)
      ;; (close! go-fft)
      (dissoc this :glue)
      (prn "Stopped Glue go-loops")
      this)))

(defn create-glue []
  (Glue.))

