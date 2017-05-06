(ns speech.glue
  (:require [clojure.core.async :refer [<! go-loop]]
            [speech
             [fft :refer [get-fft]]
             [microphone :refer [averages-channel]]
             [web :refer [add-data-to-buffer-and-maybe-send ws-send]]]))

(go-loop []
  (-> averages-channel
      <!
      add-data-to-buffer-and-maybe-send)
  (recur))

(go-loop []
  (ws-send {:fft (get-fft)})
  (recur))
