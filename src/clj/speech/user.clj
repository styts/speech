(ns speech.user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [speech.systems :refer [dev-system]]))

(comment
  (println "USER NS evaluated")

  (set-init! #'dev-system)

  (reset))
