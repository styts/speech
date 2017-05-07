(ns speech.drawing
  (:require [thi.ng.color.gradients :as grad]
            [thi.ng.color.core :as col]
            [speech.parameters :as parameters]))

(defn random-color "just for easier debugging" []
  (let [colors ["red" "blue" "green" "cyan" "#ccc" "#aaa" "#999"]]
    (rand-nth colors)))


;; http://dev.thi.ng/gradients/
;; orange
;; (def cosines [[0.500 0.500 0.500] [0.500 0.500 0.500] [0.800 0.800 0.500] [0.000 0.200 0.500]]) ;; orange
;; blue - magenta - orange
(def cosines [[0.938 0.328 0.718] [0.659 0.438 0.328] [0.388 0.388 0.296] [2.538 2.478 0.168]])
;; b/w - boring
;; (def cosines [[0.500 0.500 0.500] [0.500 0.500 0.500] [0.500 0.500 0.500] [1.000 1.000 1.000]])
(def gradient (grad/cosine-gradient (:max-value parameters/fft) cosines))

(defn get-color [val]
  ;; (js* "debugger")
  (if (or (< val 0) (>= val (:max-value parameters/fft))) "#faa"
      @(col/as-css (nth gradient (int val)))))

(defn clear-canvas! [id]
  (let [canvas (.getElementById js/document id)
        ctx (.getContext canvas "2d")
        w (.-width canvas)
        h (.-height canvas)]
    (.clearRect ctx 0 0 w h)))
