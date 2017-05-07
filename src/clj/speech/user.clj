(ns speech.user
  (:require [clojure.core.async :refer [<!! close! timeout]]
            [com.stuartsierra.component :as component]
            [speech
             [microphone :refer [audio-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]]
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
  (let [t (timeout 1000)]
    (<!! t)
    (ws-send {:frame (<!! audio-channel)}))


  ;; stopping the go-blocks is not working yet
  (close! (:go-avg (:glue (:glue system))))
  (component/stop (:glue (:glue system)))
  (component/stop (:microphone system))
)
