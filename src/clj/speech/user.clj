(ns speech.user
  (:require [clojure.core.async :refer [<!!]]
            [speech
             [microphone :refer [averages-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]]
            [system.repl :refer [reset set-init! start stop]]))

(set-init! #'dev-system)

(comment
  (reset)

  (start)
  (stop)

  (ws-send {:raw [30 60]})

  (ws-send "test")
)
