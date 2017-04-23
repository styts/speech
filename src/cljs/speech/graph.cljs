(ns speech.graph
  (:require [clojure.core :refer [swap!]]
            [reagent.core :as reagent]
            [cljsjs.chartist]))

(def chart-options {:height "200px"
                    :fullWidth true
                    :showArea true
                    :showPoint false
                    :lineSmooth false
                    :axisX {:showGrid false}
                    :axisY {:showGrid false}
                    :low 0})

(defn mount-chart [this]
  (def chart (js/Chartist.Line.
              (reagent/dom-node this)
              (clj->js {})
              (clj->js chart-options))))

(defn update-chart [new-data]
  (. chart update (clj->js {:series [new-data]})))

(defn chart-component [buffer]
    (reagent/create-class
     {:component-did-mount mount-chart
      :display-name        "chart-component"
      :reagent-render      (fn [] [:div {:class "ct-chart"}])}))
