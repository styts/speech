(ns speech.windowing
  (:require [speech.parameters :as parameters]))

(defn hamming-window-fn
  "Compute the i-th term of the hamming window of size N

  https://en.wikipedia.org/wiki/Window_function#Hamming_window"
  [N i]
  (let [a (double 0.53836)
        b (- 1 a)
        L (- N 1)
        num (* 2 Math/PI i)
        den L
        cos-term (/ num den)]
    (- a (* b (Math/cos cos-term)))))

(defn build-hamming-window
  "N would be the size of the window used for FFT"

  [N]
  (map #(hamming-window-fn N %) (range N)))

(def hamming-window (build-hamming-window (:n-bins parameters/fft)))
