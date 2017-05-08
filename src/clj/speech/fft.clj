(ns speech.fft
  (:require [cfft.core :refer [fft]]
            [clojure.core
             [async :refer [<!!]]
             [matrix :refer [to-nested-vectors]]]
            [speech
             [microphone :refer [audio-channel]]
             [utils :refer [first-half]]
             [windowing :refer [hammer]]]))

(defn prepare-fft
  "Prepare fft data: sqrt(x*x + y*y)" [x]
  (let [r (:real x)
        i (:imag x)
        m (+ (* r r) (* i i))
        r (Math/sqrt m)
        b (Math/log10 r)]
    b))

(defn clean-fft [fft-data]
  (map prepare-fft (first-half fft-data)))

(defn get-fft
  "Read off the channel and pre-processes"
  ([] (get-fft (<!! audio-channel)))
  ([raw-data]
   (let [hmrd (to-nested-vectors (hammer raw-data))
         fft-data (fft hmrd)
         a (clean-fft fft-data)]
     a)))

