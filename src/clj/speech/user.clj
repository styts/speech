
(ns speech.user
  (:require [clojure.core.async :refer [close!]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [clean-fft]]
             [glue :refer [send-frame]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]
             [windowing :refer [hamming-window]]]
            [system.repl :refer [reset set-init! start stop system]]))

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
  (send-frame 200)
  (clean-fft [1 2 34])
  (ws-send {:power hamming-window})

  ;; stopping the go-blocks is not working yet
  (close! (:go-avg (:glue (:glue system))))
  (component/stop (:glue (:glue system)))
  (component/stop (:microphone system)))
