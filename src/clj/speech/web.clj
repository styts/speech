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
             [parameters :as parameters]
             ]))

(defonce channel-hub (atom {}))

(defn ws-handler [req]
  (with-channel req channel
    (info "ws channel opened" channel)
    ;; store the channel in atom
    (swap! channel-hub assoc channel req)
    (on-close channel (fn [status] (info "ws channel closed")))))

(defn send-data-to-ws
  "Expects a string"
  [data]
  (doseq [channel (keys @channel-hub)]
    (send! channel data)))

(defn ws-send
  "Shortcut for talking to the websocket connection"
  [data]
  (send-data-to-ws (generate-string data)))

(defroutes app
  (GET "/ws" [] ws-handler)
  ;; (GET "/live" [] live-handler)
  (not-found "<h1>Page not found</h1>"))

