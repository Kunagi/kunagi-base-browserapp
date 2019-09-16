(ns kunagi-base-browserapp.components
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   [mui-commons.api :refer [<subscribe]]))

(defn ConnectionStatusIndicator []
  (let [state (<subscribe [:http-async/state])
        open? (-> state :open?)]
    (if open?
      [:> mui/IconButton
       {:disabled true
        :title "Online"
        :style {:color :grey}}
       [:> icons/Link]]
      [:> mui/IconButton
       {:disabled true
        :title "Offline"
        :style {:color :red}}
       [:> icons/LinkOff]])))
