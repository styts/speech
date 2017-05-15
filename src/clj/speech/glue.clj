(ns speech.glue
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [>!! alts!! chan sliding-buffer thread]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [process-fft]]
             [microphone :refer [audio-channel]]
             [web :refer [ws-send]]
             [windowing :refer [splitter]]]
            [system.repl :refer [start stop]]))

(def window-channel (chan (sliding-buffer 1)))

(defn go-window! []
  (splitter audio-channel window-channel 2))

(defn fft-handler [windowed-data]
  (let [a (process-fft windowed-data)]
    (ws-send {:fft [(map float a)]})))

;; These three constructs make it possible to start a process that listens to a
;; channel and calls a handler function. In addition, this process can be
;; stopped gracefully
(defn start-processor [input-ch terminate-ch process-input]
  (thread
    (loop []
      (let [[v ch] (alts!! [input-ch terminate-ch])]
        (if (identical? ch input-ch)
          (if (some? v)
            (do (process-input v)
                (recur)))
          (prn "channel terminated"))))))

(defn process-wrapper! [in-channel handler]
  (let [terminator (chan)]
    (start-processor in-channel terminator handler)
    terminator))

(defn stop-process! [terminator-ch]
  (>!! terminator-ch :finish))

;; This component starts the loops that read/write from/to the data channels
(defrecord Glue []
  component/Lifecycle
  (start [this]
    (prn "Started Glue component")
    (assoc this :glue
           {:raw-to-window (go-window!)
            :fft-to-ws (process-wrapper! window-channel fft-handler)}))
  (stop [this]
    (let [{:keys [raw-to-window fft-to-ws]} (:glue this)]
      (stop-process! fft-to-ws)
      (dissoc this :glue)
      (prn "Stopped Glue component")
      this)))

(defn create-glue []
  (Glue.))

