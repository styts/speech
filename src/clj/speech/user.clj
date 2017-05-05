(ns speech.user
  (:require [cheshire.core :refer [generate-string]]
            [speech
             [systems :refer [dev-system]]
             [web :refer [send-data-to-ws]]]
            [system.repl :refer [reset set-init! start stop]]))

(set-init! #'dev-system)

(defn ws-send
  "shortcut for talking to websocket connection"
  [data]
  (send-data-to-ws (generate-string data)))

(comment
  (reset)

  (start)
  (stop)

  (ws-send {:raw [30 60]})

  (ws-send "test")
)
