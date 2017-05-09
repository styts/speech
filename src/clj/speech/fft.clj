(ns speech.fft
  (:require [clojure.core
             [async :refer [<!!]]
             [matrix :refer [to-nested-vectors]]]
            [speech
             [microphone :refer [audio-channel]]
             [utils :refer [first-half]]
             [windowing :refer [hammer]]]
            [taoensso.tufte :refer [p]])
  (:import org.jtransforms.fft.DoubleFFT_1D))

(defn j-fft
  "A performant native-Java alternative to the old cfft library.

  100x speed increase!"
  [data]
  (let [target (double-array data) ;; Note: defining these outside might give a speed increase
        fft    (DoubleFFT_1D. (count data))]
    (.realForward fft target) ;; Mutates target!
    (vec target)))

(defn prepare-fft
  "Prepare fft data: sqrt(x*x + y*y)" [x]
  (let [m (* x x)
        r (Math/sqrt m)
        b (Math/log10 r)]
    b))

(defn get-fft
  "Read off the channel and pre-processes"
  ([] (get-fft (<!! audio-channel)))
  ([raw-data]
   (let [hmrd (to-nested-vectors (hammer raw-data))
         fft-data (j-fft hmrd)
         a (map prepare-fft fft-data)]
     a)))

