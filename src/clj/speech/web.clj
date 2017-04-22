(ns speech.web
  (:require [boot.util :refer [info]]
            clojure.core
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [org.httpkit.server :refer [on-close send! with-channel]]))

(defonce channel-hub (atom {}))

(defn- ws-handler [req]
  (with-channel req channel
    (info "channel opened" channel)
    (swap! channel-hub {}) ;; force this to be the only channel
    (swap! channel-hub assoc channel req) ;; store the channel in atom
    (on-close channel (fn [status] (info "channel closed")))))

(defn send-data-to-ws [data]
  (doseq [channel (keys @channel-hub)]
    ;; (info "sent to channel")
    (send! channel data)))

(defroutes app
  (GET "/ws" [] ws-handler)
  (not-found "<h1>Page not found</h1>"))
