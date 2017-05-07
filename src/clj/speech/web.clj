(ns speech.web
  (:require [boot.util :refer [info]]
            [cheshire.core :refer [generate-string]]
            clojure.core
            [clojure.core.async :refer [<! go-loop]]
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [org.httpkit.server :refer [on-close send! with-channel]]
            [speech
             [microphone :refer [audio-channel]]
             [parameters :as parameters]
             [utils :refer [average split-by]]]))

(defonce channel-hub (atom {}))

(defonce buffer (atom []))

(defn ws-handler [req]
  (with-channel req channel
    (info "ws channel opened" channel)
    ;; store the channel in atom
    (swap! channel-hub assoc channel req)
    (on-close channel (fn [status] (info "ws channel closed")))))

(defn live-handler [request]
  (with-channel request channel
    (info "live channel opened" channel)
    (on-close channel (fn [status] (println "live channel closed" status)))
    (go-loop []
      (let [audio (<! audio-channel)]
        (send! channel (str (doall audio) "\n") false)
        (recur)))))

(defn send-data-to-ws
  "Expects a string"
  [data]
  (doseq [channel (keys @channel-hub)]
    (send! channel data)))

(defn ws-send
  "Shortcut for talking to the websocket connection"
  [data]
  (send-data-to-ws (generate-string data)))

(defn add-data-to-buffer-and-maybe-send
  "We don't want to send every message to the websocket,
  so we buffer the messages and send a list of them.

  This is for browser performance reasons"
  [data]
  (let [n-frames   (:send-after-frames parameters/ws-grouping)
        groups-of (:groups-of parameters/ws-grouping)]
    (if (> (count @buffer) n-frames)
      (do (as-> (map average (split-by groups-of @buffer)) v
                (assoc {} :avg v)
                (ws-send v))
          (reset! buffer []))
      (swap! buffer conj data))))

(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/live" [] live-handler)
  (not-found "<h1>Page not found</h1>"))

