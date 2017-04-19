(ns speech.systems
  (:require
   [system.core :refer [defsystem]]
   [com.stuartsierra.component :as component]
   (system.components [jetty :refer [new-web-server]])
   [speech.web :refer [app]]
   [speech.microphone :refer [create-system]]
   [environ.core :refer [env]]))

(defsystem dev-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :microphone (create-system)])
