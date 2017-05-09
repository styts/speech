(ns speech.glue
  (:require [clojure.core :refer [prn]]
            [clojure.core.async :refer [>!! alts!! chan sliding-buffer thread]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [get-fft]]
             [microphone :refer [audio-channel]]
             [web :refer [ws-send]]
             [windowing :refer [splitter]]]
            [system.repl :refer [start stop]]))

(def window-channel (chan (sliding-buffer 1)))

#_(defn go-averages []
    (go-loop []
      (-> averages-channel
          <!
          add-data-to-buffer-and-maybe-send)
      (recur)))
;; (def pi (double (/ 22 7)))
;; (with-precision 3 (double (/ 22 7)))
#_(defn go-fft! []
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

(defn start-processor [input-ch terminate-ch process-input]
  (thread
    (loop []
      (let [[v ch] (alts!! [input-ch terminate-ch])]
        (if (identical? ch input-ch)
          (if (some? v)
            (do (process-input v)
                (recur)))
          (prn "channel terminated"))))))

(defn fft-handler [windowed-data]
  (let [a (get-fft windowed-data)]
    (ws-send {:fft [(map float a)]})))

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

