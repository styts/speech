(ns speech.user
  (:require [clojure.core.async :refer [<!! close!]]
            [com.stuartsierra.component :as component]
            [speech
             [microphone :refer [audio-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]]
            [system.repl :refer [reset set-init! start stop system]]))

(set-init! #'dev-system)

(comment
  (reset)

  (start)
  (stop)
  (ws-send {:fft [30 60 180 360 500 900 1400]})
  (ws-send "test")
  (ws-send {:live (<!! audio-channel)})

  ;; stopping the go-blocks is not working yet
  (close! (:go-avg (:glue (:glue system))))
  (component/stop (:glue (:glue system)))
  (component/stop (:microphone system))
)
