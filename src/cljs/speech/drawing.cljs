(ns speech.drawing
  (:require [thi.ng.color.gradients :as grad]
            [thi.ng.color.core :as col]
            [speech.parameters :as parameters]))

(defn random-color "just for easier debugging" []
  (let [colors ["red" "blue" "green" "cyan" "#ccc" "#aaa" "#999"]]
    (rand-nth colors)))

(def gradient (grad/cosine-gradient
               (:max-value parameters/fft) [[0.500 0.500 0.500] [0.500 0.500 0.500] [0.800 0.800 0.500] [0.000 0.200 0.500]]))

(defn get-color [val]
  ;; (js* "debugger")
  (if (or (< val 0) (>= val (:max-value parameters/fft))) "#eee"
      @(col/as-css (nth gradient (int val)))))

(defn clear-canvas! [id]
  (let [canvas (.getElementById js/document id)
        ctx (.getContext canvas "2d")
        w (.-width canvas)
        h (.-height canvas)]
    (.clearRect ctx 0 0 w h)))
