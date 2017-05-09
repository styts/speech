(ns speech.windowing
  (:require [clojure.core
             [async :as a :refer [<! <!! chan go put!]]
             [matrix :refer [array mul set-current-implementation]]]
            [speech
             [parameters :as parameters]]))

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
  #_(assert (= (count data)
               (:n-bins parameters/fft))
            "data and hamming-window size mismatch")
  (mul (array data) (take (count data) hamming-window)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn splitter
  "Only tested with fraction == 2
  TODO write description and add tests"
  [input-channel output-channel fraction]
  (let [state (atom [])]
    (go (while true
          (let [audio-data (<! input-channel)
                size (/ (count audio-data) fraction)
                a (take size audio-data)  ; first half A
                b (drop size audio-data)] ; second half B
            ;; (println "audio:" (take 10 audio-data)) ; debugging
            (put! output-channel audio-data) ;; pass the whole thing
            ;; (println "state has:" @state) ; debugging
            (if (= (count @state) (- fraction 1)) ; if we saved an item
              (do
                ;; combine B and A - and send them out
                (swap! state conj a)
                (assert (= fraction (count @state))
                        (str "expects" fraction "elements"))
                (put! output-channel (flatten @state))
                (reset! state [])))
            (swap! state conj b))))))

(comment
  (def buffered-channel (chan))
  (debug-channel "buffered channel:" buffered-channel)

  (def foo-channel (chan))

  (splitter foo-channel buffered-channel 2)
  (put! foo-channel (range 4))

  (splitter audio-channel buffered-channel 2)
  )
