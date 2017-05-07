(ns speech.user
  (:require [cfft.core :refer [fft]]
            [clojure.core.async :refer [<!! close! timeout]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [prepare-fft]]
             [microphone :refer [audio-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]]
            [system.repl :refer [reset set-init! start stop system]]))

(defn send-frame []
  (let [t (timeout 500)]
    (<!! t)
    (let [data (<!! audio-channel)]
      (ws-send {:frame data :power (map prepare-fft (fft data))}))))

(comment
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; START SYSTEM:
  (set-init! #'dev-system)
  (start)
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (reset)
  (stop)
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  ;; Experiments:
  (send-frame)

  ;; stopping the go-blocks is not working yet
  (close! (:go-avg (:glue (:glue system))))
  (component/stop (:glue (:glue system)))
  (component/stop (:microphone system)))
