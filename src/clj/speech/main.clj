(ns speech.main
  (:gen-class)
  (:require [speech.microphone :refer [capture]]))

(defn -main [& args]
  (println "Starting capture:")
  (speech.microphone/capture))
