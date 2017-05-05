(ns speech.app
  (:require [chord.client :refer [ws-ch]]
            [clojure.core :refer [swap!]]
            [clojure.core.async :refer [<!]]
            [reagent.core :as reagent]
            [speech.canvas :refer [canvas-component]]
            [speech.graph :refer [chart-component update-chart]])

  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def chart-size 200)
(defonce buffer (reagent.ratom/atom (repeat chart-size 0)))

(enable-console-print!)

(defn calling-component []
  [:div
   [chart-component buffer]
   [canvas-component]])

(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))

(defn add-message [buffer message]
  (take chart-size (conj buffer message)))

(defn receive-msgs! [server-ch]
  ;; every time we get a message from the server, add it to our list
  (go-loop []
    (let [{:keys [message error] :as msg} (<! server-ch)]
      (if error
        (js/console.error error)
        (do
          (doseq [m message] (swap! buffer add-message m))
          ;; (swap! buffer add-message message)
          (update-chart (reverse @buffer))))
      (when msg
        (recur)))))

(set!
 (.-onload js/window)
 (fn []
   (go
     (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:4000/ws" {:format :json-kw}))]
       (js/console.log "connected to channel: " ws-channel)
       (if error
         (js/console.log "Couldn't connect to websocket: " error)
         ;; register handler for listening to ws
         (receive-msgs! ws-channel))))))
