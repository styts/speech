(ns speech.frames
  (:require [cljsjs.chartist]
            [clojure.core :refer [prn]]))

(enable-console-print!)

(def chart-options {:height "200px"
                    :fullWidth true
                    :showArea true
                    :showPoint false
                    :lineSmooth false
                    :axisX {:showGrid false}
                    :axisY {:showGrid false}
                    })

(defn mount-chart [element data]
  (def chart (js/Chartist.Line.
              element
              (clj->js {:series [data]})
              (clj->js chart-options))))

(defn draw-live-frame [frame element-id]
  (let [chart (.getElementById js/document element-id)]
    (mount-chart chart frame)))

