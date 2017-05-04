(ns speech.web
  (:require [boot.util :refer [info]]
            [cheshire.core :refer [generate-string]]
            clojure.core
            [clojure.core.async :refer [<! go-loop]]
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [not-found]]]
            [speech.utils :refer [average split-by]]
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
        (send! channel (str (doall audio) "\n") false)
        (recur)))))

(defn send-data-to-ws [data]
  (doseq [channel (keys @channel-hub)]
    (send! channel data)))

(defn add-data-to-buffer-and-maybe-send [data]
  (if (> (count @buffer) 50)
    (do
      (-> average
          (map (split-by 15 @buffer))
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

  (GET "/live" [] live-handler)

  (not-found "<h1>Page not found</h1>"))

