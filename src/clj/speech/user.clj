(ns speech.user
  (:require [clojure.core.async :as a :refer [<!! alts!! chan close! put! thread]]
            [com.stuartsierra.component :as component]
            [speech
             [fft :refer [j-fft prepare-fft]]
             [glue :refer [window-channel]]
             [systems :refer [dev-system]]
             [web :refer [ws-send]]
             [windowing :refer [hammer]]]
            [system.repl :refer [reset set-init! start stop system]]
            [taoensso.tufte :as tufte :refer [p profile]]))

(defn consecutive-frames []
  (let [a (<!! window-channel)
        b (<!! window-channel)]
    (ws-send {:power b
              :frame a})))
(defn snd
  "Display charts for debugging"
  ([]  (snd (<!! window-channel)))
  ([d] (let [pwr (map prepare-fft (j-fft (hammer d)))]
         (ws-send {:fft [pwr]
                   :frame d
                   :green (hammer d)
                   :power pwr}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Evaluating this file will START SYSTEM:
(set-init! #'dev-system)
(start)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Actions:
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (snd [0 1 2 3 0 1 2 3 0 1 2 3 0 1 2 3])
  (snd)

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Profiling
  (tufte/add-basic-println-handler! {}) ;; enable printing to stdout
  (profile {} (dotimes [_ 5]
                (p :jfft (doall (j-fft (range 256))))))

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Commands that work, but not currently needed
  (consecutive-frames)

  (reset)
  (stop)
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Here be dragons...
  ;; stopping the go-blocks is not working yet
  (component/stop (:glue system))
  (component/start (:glue system))

)

