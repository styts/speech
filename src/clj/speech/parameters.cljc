(ns speech.parameters)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for microphone
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^{:doc "Audio card needs to capture with this frequency"}
  sampling-rate-hz 16000)

(def ^{:doc "Frame size in milliseconds. Research shows it should be 10-20ms"}
  frame-size-ms 20)

(def frames-per-second (/ 1000 frame-size-ms)) ;; 50 frames per second

(def samples-per-frame (/ sampling-rate-hz frames-per-second)) ;; 320 samples at 20ms
(def bytes-per-frame (* 2 samples-per-frame)) ;; two bytes per sample

(def fft-frequencies (map #(/ (* sampling-rate-hz %) samples-per-frame)
                          (range samples-per-frame)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for web server
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ws-grouping {:send-after-frames 5 ;; high - less cpu, more delay
                  :groups-of 3})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for displaying
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def canvas {;; :show-seconds 5 ;; not implemented
             :capacity 500
             :spectrogram-capacity 200
             :max-volume 1500})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for fft
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def fft {:max-value 5}) ;; arbitrary, values above won't be colored (used to build gradient)

(def spectrogram {:width-px 1000 :height-px 400})
