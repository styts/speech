(ns speech.styles
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:font-family "Helvetica Neue"
          :font-size   "16px"
          :line-height 1.5}]

  [:h1 {:font-size "16px"
        :font-weight "normal"
        :margin "0px"
        :color "#ddd"}]

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
  [:canvas#spectrogram {:width "100%"}])
