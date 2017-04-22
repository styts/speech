(ns speech.web
  (:require [boot.util :refer [info]]
            [cheshire.core :refer [generate-string]]
            clojure.core
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [org.httpkit.server :refer [on-close send! with-channel]]))

(defonce channel-hub (atom {}))

(defn hello-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World!"})

(defn- ws-handler [req]
  (with-channel req channel
    (info "channel opened" channel)
    (swap! channel-hub assoc channel req) ;; store the channel
    (on-close channel (fn [status] (info "channel closed")))))

(defn send-data-to-ws [data]
  (doseq [channel (keys @channel-hub)]
    ;; (info "sent to channel")
    (send! channel data)))

(comment
  (swap! channel-hub {})
  (count @channel-hub)
  (send-data-to-ws (generate-string [1 2 3])))

(defroutes app
  (GET "/" [] hello-handler)
  (GET "/ws" [] ws-handler)
  (not-found "<h1>Page not found</h1>")
  ;; (resources "/")
)
