(ns speech.systems
  (:require
   [system.core :refer [defsystem]]
   [com.stuartsierra.component :as component]
   (system.components [http-kit :refer [new-web-server]])
   [speech.web :refer [app]]
   [speech.glue] ;; referring this starts the go loops
   [speech.microphone :refer [create-system]]
   [environ.core :refer [env]]))

(defsystem dev-system
  [:web (new-web-server (Integer. (env :http-port "4000")) app)
   :microphone (create-system)])
