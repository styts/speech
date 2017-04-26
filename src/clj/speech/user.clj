(ns speech.user
  (:require [cheshire.core :refer [generate-string]]
            [speech
             [systems :refer [dev-system]]
             [web :refer [add-data-to-buffer-and-maybe-send buffer send-data-to-ws]]]
            [system.repl :refer [reset set-init! start stop]]))

(comment
  (set-init! #'dev-system)

  (reset)
  (start)
  (stop)
  (+ 1 2)

  (send-data-to-ws (generate-string [1 2]))
  (add-data-to-buffer-and-maybe-send (generate-string [1 2]))

  (send-data-to-ws (generate-string {:foo "bar"}))
  @buffer
  (count @buffer)
)
