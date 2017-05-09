(ns speech.glue
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [<! <!! chan go go-loop]]
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
    (let [a (get-fft (<! window-channel))
          b (get-fft (<! window-channel))
          c (get-fft (<! window-channel))
          d (get-fft (<! window-channel))]
      (ws-send {:fft [(map float a)
                      (map float b)
                      (map float c)
                      (map float d)]})
      (recur))))

#_(go
    (let [capture (dotimes [_ 3] (<!! window-channel))]
      capture))

(defn go-window! []
  (splitter audio-channel window-channel 2))


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
      (dissoc this :glue)
      (prn "Stopped Glue go-loops")
      this)))

(defn create-glue []
  (Glue.))

