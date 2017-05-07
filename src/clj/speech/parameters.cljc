(ns speech.parameters)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for microphone
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^{:doc "Audio card needs to capture with this frequency"}
  sampling-rate-hz 8000)

(def ^{:doc "Frame size in milliseconds. Research shows it should be 10-20ms"}
  frame-size-ms 20)

(def samples-per-frame (/ 1000 frame-size-ms)) ;; 50 samples at 20ms

(def frames-per-second (/ 1000 samples-per-frame)) ;; 20 frames per second

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for web server
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ws-grouping {:send-after-frames 25 ;; high - less cpu, more delay
                  :groups-of 5})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for displaying
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def canvas {;; :show-seconds 5 ;; not implemented
             :capacity 1000
             :spectrogram-capacity 200
             :max-volume 80})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for fft
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def fft {:max-value 2000}) ;; arbitrary, values above won't be colored (used to build gradient)

(def spectrogram {:width-px 1000 :height-px 40})
