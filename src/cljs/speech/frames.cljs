(ns speech.frames
  (:require [cljsjs.chartist]
            [clojure.core :refer [prn]]))

(enable-console-print!)

(def chart-options {:height "160px"
                    :fullWidth true
                    :showArea true
                    :showPoint false
                    :lineSmooth false
                    :axisX {:showGrid false}
                    :axisY {:showGrid false}})

(defn mount-chart [element data & type]
  (let [cls (if (= (first (first type)) :bar) js/Chartist.Bar js/Chartist.Line)]
    (new cls
         element
         (clj->js {:series [data]})
         (clj->js chart-options))))

(defn draw-live-frame [frame element-id & type]
  (let [chart (.getElementById js/document element-id)]
    (mount-chart chart frame type)))

