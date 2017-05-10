(ns speech.styles
  (:require [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles screen
  [:body {:font-family "Helvetica Neue"
          :font-size   "16px"
          :line-height 1.5}]

  [:#frame-chart.ct-chart
   [:.ct-series-a
    [:.ct-line
     {:stroke "#ccf"
      :stroke-width "1px"}]
    [:.ct-area {:fill "#bbf"}]]]

  [:#green-chart.ct-chart
   [:.ct-series-a
    [:.ct-line
     {:stroke "#cfc"
      :stroke-width "1px"}]
    [:.ct-area {:fill "#bfb"}]]]

  [:#power-chart.ct-chart
   [:.ct-series-a
    [:.ct-bar
     {:stroke "#fbb"
      :stroke-width "1px"}]
    [:.ct-area {:fill "#fbb"}]]]

  [:canvas#canvas {:width "100%"}]
  [:canvas#spectrogram {:width "100%"}]
  )
