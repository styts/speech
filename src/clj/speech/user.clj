(ns speech.user
  (:require [speech
             [systems :refer [dev-system]]
             [web :refer [ws-send]]]
            [system.repl :refer [reset set-init! start stop]]))

(set-init! #'dev-system)

(comment
  (reset)

  (start)
  (stop)

  (ws-send {:fft [30 60 180 360 500 900 1400]})
  (ws-send "test")
)
