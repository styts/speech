(ns speech.frames
  (:require [clojure.core :refer [prn]]))

(enable-console-print!)

(def chart-options {:height "300px"
                    :fullWidth true
                    :showArea true
                    :showPoint false
                    :lineSmooth false
                    :axisX {:showGrid false}
                    :axisY {:showGrid false}
                    ;; :low 0
                    })

(defn mount-chart [element data]
  (def chart (js/Chartist.Line.
              element
              (clj->js {:series [data]})
              (clj->js chart-options))))

(defn draw-live-frame [frame]
  (prn frame)
  (let [cnt (.getElementById js/document "container")
        chart (.getElementById js/document "frame-chart")
        ]
    (mount-chart chart frame)))
