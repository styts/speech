(ns speech.main
  (:gen-class)
  (:require [speech.microphone :refer [start-capture]]))

(defn -main [& args]
  (println "Starting capture:")
  (speech.microphone/start-capture))
