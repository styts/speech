(ns speech.fft
  (:require [cfft.core :refer [fft]]
            [clojure.core
             [async :refer [<!!]]
             [matrix :refer [to-nested-vectors]]]
            [speech
             [microphone :refer [audio-channel]]
             [utils :refer [first-half]]
             [windowing :refer [hammer]]]
            [taoensso.tufte :refer [p]]))

(defn prepare-fft
  "Prepare fft data: sqrt(x*x + y*y)" [x]
  (let [r (:real x)
        i (:imag x)
        m (+ (* r r) (* i i))
        r (Math/sqrt m)
        b (Math/log10 r)]
    b))

(defn clean-fft [fft-data]
  (map (p :prep prepare-fft) (p :fst-hlf (first-half (doall fft-data)))))

(defn get-fft
  "Read off the channel and pre-processes"
  ([] (get-fft (p :take-audio (<!! audio-channel))))
  ([raw-data]
   (let [hmrd (p :tnv (to-nested-vectors (p :hammer (doall (hammer raw-data)))))
         fft-data (p :fft (doall (p :fft-pre-doall (fft hmrd))))
         a (p :cln (clean-fft fft-data))]
     a)))

