(ns speech.app
  (:require [chord.client :refer [ws-ch]]
            [clojure.core.async :refer [<!]]
            [reagent.core :as reagent]
            [speech.canvas :refer [canvas-component push-raw-data]]
            [speech.frames :refer [draw-live-frame]]
            [speech.spectrogram
             :refer
             [add-data-to-spectrogram spectrogram-component]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn container []
  [:div
   [spectrogram-component]])

(defn init []
  (reagent/render-component [container] (.getElementById js/document "container")))

(defn- handle-message! [message]
  (let [fft   (:fft message)
        ffts  (:ffts message)
        power (:power message)
        green (:green message)
        avg   (:avg message)
        frame (:frame message)]
    (if fft   (add-data-to-spectrogram fft))
    (if avg   (push-raw-data avg))
    (if power (draw-live-frame power "power-chart" :bar))
    (if frame (draw-live-frame frame "frame-chart"))
    (if green (draw-live-frame green "green-chart"))))

(defn receive-msgs!
  "Every time we get a message from the server, add it to our list"
  [server-ch]
  (go-loop []
    (let [{:keys [message error] :as msg} (<! server-ch)]
      (if error
        (js/console.error error)
        (handle-message! message))
      (when msg
        (recur)))))

(defn connect-to-ws!
  "Connects to WS and registers message listener"
  []
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:4000/ws" {:format :json-kw}))]
      (js/console.log "connected to channel: " ws-channel)
      (if error
        (js/console.log "Couldn't connect to websocket: " error)
        ;; register handler for listening to ws
        (receive-msgs! ws-channel)))))

(set! (.-onload js/window) connect-to-ws!)
