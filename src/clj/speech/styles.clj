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

  [:#power-chart.ct-chart
   [:.ct-series-a
    [:.ct-line
     {:stroke "#fcc"
      :stroke-width "1px"}]
    [:.ct-area {:fill "#fbb"}]]]

  [:#green-chart.ct-chart
   [:.ct-series-a
    [:.ct-line
     {:stroke "#cfc"
      :stroke-width "1px"}]
    [:.ct-area {:fill "#bfb"}]]]


  [:canvas#canvas {:width "100%"}]
  [:canvas#spectrogram {:width "100%"}]
  )
