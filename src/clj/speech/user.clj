(ns speech.user
  (:require [clojure.core
             [async :refer [<!! close!]]
             [matrix :refer [to-nested-vectors]]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [get-fft j-fft]]
             [glue :refer [window-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]
             [windowing :refer [hammer hamming-window]]]
            [system.repl :refer [reset set-init! start stop system]]
            [taoensso.tufte :as tufte :refer [p profile]]))

(defn send-both []
  (let [d (<!! window-channel)]
    (ws-send {:power (get-fft d)
              :frame (to-nested-vectors (hammer d))})))

(defn consecutive-frames []
  (let [a (<!! window-channel)
        b (<!! window-channel)]
    (ws-send {:power b
              :frame a})))

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
  ;; display the hamming-window
  (ws-send {:power (to-nested-vectors hamming-window)})
  ;; send both the frame and power spectrum for charting
  (send-both)
  (consecutive-frames)

  ;; Profiling
  (tufte/add-basic-println-handler! {}) ;; enable printing to stdout
  (profile {} (dotimes [_ 5]
                (p :jfft (doall (j-fft (range 256))))))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Here be dragons...
  ;; stopping the go-blocks is not working yet
  (close! (:go-avg (:glue (:glue system))))
  (component/stop (:glue (:glue system)))
  (component/stop (:microphone system)))
