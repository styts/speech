(ns speech.web
  (:require [boot.util :refer [info]]
            [cheshire.core :refer [generate-string]]
            clojure.core
            [clojure.core.async :refer [<! go-loop]]
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [org.httpkit
             [server :refer [close on-close send! with-channel]]
             [timer :refer [schedule-task]]]
            [speech.microphone :refer [audio-channel averages-channel]]))

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
        (send! channel (str audio "\n") false)
        (recur)))))

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
  (if (> (count @buffer) 20)
    (do
      (-> average
          (map (split-by 10 @buffer))
          generate-string
          send-data-to-ws)
      (reset! buffer []))
    (swap! buffer conj data)))

(go-loop []
  (-> averages-channel
      <!
      add-data-to-buffer-and-maybe-send)
  (recur))

(defroutes app
  (GET "/ws" [] ws-handler)

  (GET "/stream" [] stream-handler)
  (GET "/live" [] live-handler)

  (not-found "<h1>Page not found</h1>"))

