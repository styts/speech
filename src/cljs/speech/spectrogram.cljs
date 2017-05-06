(ns speech.spectrogram
  (:require [reagent.core :as reagent]
            [speech.drawing :refer [clear-canvas! get-color]]
            [speech.parameters :as parameters]))

(enable-console-print!)

;; width and height, in px
(def w 1000)
(def h 400)

(def params parameters/canvas)

;; data is stored here
(def capacity (:spectrogram-capacity params)) ;; buffer capacity
(def spectrogram-buffer (reagent/atom []))

(defn spectrogram-component []
  (reagent/create-class
   {:reagent-render (fn [] [:canvas#spectrogram {:width w :height h}])
    :component-did-mount (fn [this]
                           (let [ctx (.getContext (reagent/dom-node this) "2d")]
                           ;; set fill color
                             (set! (.-fillStyle ctx) "#ccc")

                           ;; set origin to lower-left
                             (do
                               (.translate ctx 0 h)
                               (.scale ctx 1 -1))))}))

(defn add-to-buffer
  "if it has reached capacity, then clear canvas and return [message]
  otherwise (take n)"
  [b message]
  (let [has-reached-capacity? (>= (count b) capacity)]
    (if has-reached-capacity?
      (do (clear-canvas! "spectrogram") [message])
      (take capacity (conj b message)))))

(defn draw-rect [ctx x-idx y-idx value data-size]
  (let [rw (/ w capacity)
        rh (/ h data-size)
        x (* rw x-idx) ;; FIXME needs to depend on width and capacity
        y (* rh y-idx)
        ]
    (set! (.-fillStyle ctx) (get-color value))
    (.fillRect ctx x y rw rh)))

(defn draw-spectrogram [new-data]
  (let [canvas (.getElementById js/document "spectrogram")
        ctx    (.getContext canvas "2d")
        idx    (- (count @spectrogram-buffer) 1)]
    (doseq [[i value] (map-indexed vector new-data)]
      (draw-rect ctx idx i value (count new-data)))))

;; public API:

(defn add-data-to-spectrogram
  "Save the message in the canvas buffer.
  Draw the last message on the canvas"
  [data]
  (swap! spectrogram-buffer add-to-buffer data)
  (draw-spectrogram data))
