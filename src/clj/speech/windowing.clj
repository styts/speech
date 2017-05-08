(ns speech.windowing
  (:require [clojure.core.matrix :refer [array mul set-current-implementation]]
            [speech.parameters :as parameters]))

(set-current-implementation :vectorz)

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

(def hamming-window (array (build-hamming-window (:n-bins parameters/fft))))

(defn hammer
  "Multiplies the data vector with the hamming window vector,
  thus applying the hammming window function (a hill from 0 to 1)"
  [data]
  (assert (= (count data)
             (:n-bins parameters/fft))
          "data and hamming-window size mismatch")
  (mul (array data) hamming-window))
