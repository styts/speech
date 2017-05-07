(ns speech.fft
  (:require [cfft.core :refer [fft]]
            [clojure.core.async :refer [<!!]]
            [speech
             [microphone :refer [audio-channel]]
             [utils :refer [first-half]]]))

(defn prepare-fft
  "Prepare fft data: sqrt(x*x + y*y)" [x]
  (let [r (:real x)
        i (:imag x)
        m (+ (* r r) (* i i))
        r (Math/sqrt m)
        b (Math/log10 r)]
    b))

(defn clean-fft [fft-data]
  (first-half (map prepare-fft fft-data)))

(defn get-fft
  "Read off the channel and pre-processes"
  []
  (let [ffts (fft (<!! audio-channel))
        a (clean-fft ffts)]
    a))

