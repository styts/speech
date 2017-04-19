(ns speech.web
  (:require [boot.util :refer [info]]
            clojure.core
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [org.httpkit.server :refer [on-close on-receive send! with-channel]]))

(def channel-hub (atom {}))

(defn hello-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World!"})

(defn- ws-handler [req]
  (with-channel req channel
    (info "opened channel" channel)
    (swap! channel-hub assoc channel req) ;; store the channel
    ;; register handlers
    (on-close channel (fn [status] (info "channel closed")))
    (on-receive channel (fn [data] (send! channel data)))))

(defn send-data-to-ws [data]
  (doseq [channel (keys @channel-hub)]
    (send! channel "message sent to a channel")))

(defroutes app
  (GET "/" [] hello-handler)
  (GET "/ws" [] ws-handler)
  (not-found "<h1>Page not found</h1>")
  ;; (resources "/")
)
