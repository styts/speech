(ns speech.user
  (:require [cheshire.core :refer [generate-string]]
            [speech
             [systems :refer [dev-system]]
             [web :refer [send-data-to-ws]]]
            [system.repl :refer [reset set-init!]]))

(comment
  (set-init! #'dev-system)

  (reset)

  (send-data-to-ws (generate-string [1 2]))
  (send-data-to-ws (generate-string {:foo "bar"}))
)
