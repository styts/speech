(ns speech.fft
  (:require [cfft.core :refer [fft]]
            [clojure.core.async :refer [<!!]]
            [speech.microphone :refer [audio-channel]]))

(defn prepare-fft
  "Prepare fft data:
sqrt(x*x + y*y)" [x]
  (let [r (:real x)
        i (:imag x)
        m (+ (* r r) (* i i))]
    (Math/sqrt m)))

(defn get-fft
  "Read off the channel and pre-processes"
  []
  (let [ffts (fft (<!! audio-channel))
        a (map prepare-fft ffts)]
    a))

