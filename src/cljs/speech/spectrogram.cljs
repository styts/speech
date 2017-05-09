(ns speech.spectrogram
  (:require [reagent.core :as reagent]
            [speech.drawing :refer [clear-canvas! get-color]]
            [speech.parameters :as parameters]))

(enable-console-print!)

;; width and height, in px
(def w (:width-px parameters/spectrogram))
(def h (:height-px parameters/spectrogram))

(def params parameters/canvas)

;; data is stored here
(def capacity (:spectrogram-capacity params)) ;; buffer capacity
(def spectrogram-counter (reagent/atom 0))

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

(defn handle-counter
  [n-drawn message]
  (let [has-reached-capacity? (>= n-drawn capacity)]
    (if has-reached-capacity?
      (do (clear-canvas! "spectrogram") 0)
      (inc n-drawn))))

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
        idx    (dec @spectrogram-counter)]
    (doseq [[i value] (map-indexed vector new-data)]
      (draw-rect ctx idx i value (count new-data)))))

;; public API:

(defn add-data-to-spectrogram
  "Increment counter,
  Draw the last message on the canvas"
  [data]
  (doseq [d data]
    (swap! spectrogram-counter handle-counter d)
    (draw-spectrogram d)))
