(ns kunagi-base-browserapp.components
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   [mui-commons.components :as muic]
   [mui-commons.api :refer [<subscribe]]))


(defn- user-has-permission? [req-perm]
  (when-let [user (<subscribe [:auth/user])]
    (let [user-perms (or (-> user :user/perms)
                         #{})]
      (user-perms req-perm))))


(defn PermissionBoundary [req-perm protected-component alternative-component]
  [:div.PermsBoundary
   (if (user-has-permission? req-perm)
     protected-component
     alternative-component)])


(defn ConnectionStatusIndicator []
  (let [state (<subscribe [:http-async/state])
        open? (-> state :open?)]
    (if open?
      [:> mui/IconButton
       {:disabled true
        :title "Online"
        :size :small
        :style {:color :grey}}
       [:> icons/Link]]
      [:> mui/IconButton
       {:disabled true
        :title "Offline"
        :size :small
        :style {:color :red}}
       [:> icons/LinkOff]])))
