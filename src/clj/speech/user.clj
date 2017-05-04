(ns speech.user
  (:require [cheshire.core :refer [generate-string]]
            [clojure.core.async :refer [buffer]]
            [speech
             [systems :refer [dev-system]]
             [web :refer [add-data-to-buffer-and-maybe-send send-data-to-ws]]]
            [system.repl :refer [reset set-init! start stop]]))

(set-init! #'dev-system)

(comment
  (reset)

  (start)
  (stop)
  (+ 1 2)

  (send-data-to-ws (generate-string [1 2]))
  (add-data-to-buffer-and-maybe-send (generate-string [1 2]))

  (send-data-to-ws (generate-string {:foo "bar"}))
  @buffer
  (count @buffer)

  (buffer)
)
