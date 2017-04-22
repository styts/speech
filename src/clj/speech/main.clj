(ns speech.main
  (:gen-class)
  (:require [speech.systems :refer [prod-system]]
            [system.repl :refer [set-init! start]]))

(defn -main [& args]
  (set-init! #'prod-system)
  (start))
