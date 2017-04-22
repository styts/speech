(ns speech.user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [speech.systems :refer [dev-system]]))

(comment
  (set-init! #'dev-system)

  (reset))
