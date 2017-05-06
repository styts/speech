(ns speech.styles
  (:require [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles screen
  [:body {:font-family "Helvetica Neue"
          :font-size   "16px"
          :line-height 1.5}]

  [:.ct-chart
   [:.ct-series-a
    [:.ct-line
     {:stroke "#ccf"
      :stroke-width "1px"}]
    [:.ct-area {:fill "#bbf"}]]]

  [:canvas {;;:border "1px solid #eee"
            :width "100%"
            }]
  )
