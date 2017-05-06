(ns speech.canvas
  (:require clojure.core
            [reagent.core :as reagent]
            [speech.parameters :as parameters]))

(enable-console-print!)

(def params parameters/canvas)

;; data is stored here
(def capacity (:capacity params)) ;; buffer capacity
(def canvas-buffer (reagent/atom []))

;; canvas width and height, in px
(def w 1000)
(def h 100)

(def max-volume (:max-volume params)) ;; used for calculating height of the rectangles to draw

(def aid-debugging? false) ;; enables random colors for data samples

(defn- init
  "Executed when the component gets mounted. Some one-time initialization for
  the canvas"
  [this]
  (def ctx (.getContext (reagent/dom-node this) "2d"))

  ;; set fill color
  (set! (.-fillStyle ctx) "#ccc")

  ;; set origin to lower-left
  (do
    (.translate ctx 0 h)
    (.scale ctx 1 -1)))

(defn- clear-canvas! []
  (let [ctx (.getContext (.getElementById js/document "canvas") "2d")]
    (.clearRect ctx 0 0 w h)))

(defn- add-to-buffer
  "if it has reached capacity, then clear canvas and return [message]
  otherwise (take n)"
  [b message]
  (let [has-reached-capacity? (>= (count b) capacity)]
    (if has-reached-capacity?
      (do (clear-canvas!) [message])
      (take capacity (conj b message)))))

(defn- canvas-component []
  (reagent/create-class
   {:reagent-render (fn [] [:canvas#canvas {:width w :height h}])
    :component-did-mount init}))

(defn- rect-coords
  "Coordinates used by the ctx.fillRect function"
  [index size]
  (let [r-width   (/ w capacity)
        r-height  (* size (/ h max-volume))
        x1        (* index r-width)
        y1        0]
    [x1 y1 r-width r-height]))

(defn- random-color "just for easier debugging" []
  (let [colors ["red" "blue" "green" "cyan" "#ccc" "#aaa" "#999"]]
    (rand-nth colors)))

(defn- draw-on-canvas [new-data]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        idx    (- (count @canvas-buffer) 1)
        size   new-data
        coords (rect-coords idx size)
        x1     (nth coords 0)
        y1     (nth coords 1)
        x2     (nth coords 2)
        y2     (nth coords 3)]
    (if aid-debugging?
      (set! (.-fillStyle ctx) (random-color)))
    (.fillRect ctx x1 y1 x2 y2)))

;; public API:

(defn push-raw-data
  "Save the message in the canvas buffer.
  Draw the last message on the canvas"
  [message]
  (doseq [data message]
    (swap! canvas-buffer add-to-buffer data)
    (draw-on-canvas data)))
