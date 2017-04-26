(ns speech.web
  (:require [boot.util :refer [info]]
            [cheshire.core :refer [generate-string]]
            clojure.core
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [org.httpkit
             [server :refer [close on-close send! with-channel]]
             [timer :refer [schedule-task]]]
            [speech.utils :refer [average split-by]]))

(defonce channel-hub (atom {}))
(defonce buffer (atom []))

(defn- ws-handler [req]
  (with-channel req channel
    (info "channel opened" channel)
    (swap! channel-hub {}) ;; force this to be the only channel
    (swap! channel-hub assoc channel req) ;; store the channel in atom
    (on-close channel (fn [status] (info "channel closed")))))

(defn stream-handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed, " status)))
    (loop [id 0]
      (when (< id 2000)
        (schedule-task (* id 2)
                       (send! channel (str "message from server #" id "\n") false)) ; false => don't close after send
        (recur (inc id))))
    (schedule-task 10000 (close channel)))) ;; close in 10s.

(defn send-data-to-ws [data]
  (doseq [channel (keys @channel-hub)]
    (send! channel data)))

(defn add-data-to-buffer-and-maybe-send [data]
  (if (> (count @buffer) 400)
    (do
      (send-data-to-ws (generate-string (map average (split-by 10 @buffer))))
      (reset! buffer []))
    (swap! buffer conj data)))

(defroutes app
  (GET "/ws" [] ws-handler)

  (GET "/stream" [] stream-handler)
  (not-found "<h1>Page not found</h1>"))
