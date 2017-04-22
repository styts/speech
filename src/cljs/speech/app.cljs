(ns speech.app
  (:require [chord.client :refer [ws-ch]]
            [clojure.core :refer [pr-str]]
            [clojure.core.async :refer [<!]]
            [reagent.core :as reagent])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn calling-component []
  [:div "Hello"])

(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))

(defn receive-msgs! [server-ch]
  ;; every time we get a message from the server, add it to our list
  (go-loop []
    (let [{:keys [message error] :as msg} (<! server-ch)]
      (js/console.log message)
      (when message
        (recur)))))

(set!
 (.-onload js/window)
 (fn []
   (go
     (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:4000/ws"))]
       (js/console.log "connected to channel: " ws-channel)
       (if error
         (js/console.log "Couldn't connect to websocket: " error)
         ;; register handler for listening to ws
         (receive-msgs! ws-channel))))))
