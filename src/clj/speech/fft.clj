(ns speech.fft
  (:require [clojure.core
             [async :refer [<!!]]
             [matrix :refer [to-nested-vectors]]]
            [speech
             [windowing :refer [hammer]]])
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
  "Prepare fft data" [x]
  (let [r (Math/abs x)
        b (Math/log10 r)]
    b))

(defn process-fft
  "Pre-processes data:

  given raw data,
  apply hamming window
  apply fft
  apply abs and log10"
  ([raw-data]
   (let [hmrd (hammer raw-data)
         fft-data (j-fft hmrd)
         a (map prepare-fft fft-data)]
     a)))

