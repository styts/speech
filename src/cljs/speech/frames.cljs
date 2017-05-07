(ns speech.frames
  (:require [cljsjs.chartist]
            [clojure.core :refer [prn]]))

(enable-console-print!)

(def chart-options {:height "250px"
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

(defn draw-live-frame [frame element-id]
  (js/console.log element-id (count frame) frame)
  (let [chart (.getElementById js/document element-id)]
    (mount-chart chart frame)))

