(ns speech.app
  (:require [chord.client :refer [ws-ch]]
            [clojure.core :refer [pr-str]]
            [clojure.core.async :refer [<!]]
            [reagent.core :as reagent])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn some-component []
  [:div
   [:h3 "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."]])

(defn calling-component []
  [:div "Parent component"
   [some-component]])

(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))

;; messages from the websocket
(go
  (let [{:keys [ws-channel]} (<! (ws-ch "ws://localhost:4000/ws"))
        {:keys [message]} (<! ws-channel)]
    (js/console.log "Got message from server:" (pr-str message))))
