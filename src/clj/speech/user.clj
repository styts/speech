(ns speech.user
  (:require [cfft.core :refer [fft]]
            [clojure.core
             [async :refer [<!! close!]]
             [matrix :refer [to-nested-vectors]]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [get-fft]]
             [glue :refer [send-frame]]
             [microphone :refer [audio-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]
             [windowing :refer [hammer hamming-window]]]
            [system.repl :refer [reset set-init! start stop system]]
            [taoensso.tufte :as tufte :refer [p profile]]))

(defn- send-both []
  (let [d (<!! audio-channel)]
    (ws-send {:power (get-fft d)
              :frame (to-nested-vectors (hammer d))})))

(comment
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; START SYSTEM:
  (set-init! #'dev-system)
  (start)
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (reset)
  (stop)
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Actions:
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; send frame after a delay in ms
  (send-frame 200)
  ;; display the hamming-window
  (ws-send {:power (to-nested-vectors hamming-window)})
  ;; send both the frame and power spectrum for charting
  (send-both)

  ;; Profiling
  (tufte/add-basic-println-handler! {})
  (profile {} (dotimes [_ 5]
                (p :fft (doall (fft (range 256))))))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Here be dragons...
  ;; stopping the go-blocks is not working yet
  (close! (:go-avg (:glue (:glue system))))
  (component/stop (:glue (:glue system)))
  (component/stop (:microphone system)))
