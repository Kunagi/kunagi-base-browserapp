(ns kunagi-base-browserapp.modules.desktop.components
  (:require
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [mui-commons.api :refer [<subscribe dispatch!]]
   [mui-commons.components :as muic]
   [mui-commons.theme :as theme]))


(defn Error [[id [msg info]]]
  [muic/ErrorCard
   [:div
    {:style {:font-weight :bold}}
    msg]
   [muic/Data info]
   [:br]
   [:> mui/Button
    {:on-click #(dispatch! [:desktop/dismiss-error id])
     :color :inherit}
    "Dismiss"]])


(defn Errors []
  (let [errors (<subscribe [:desktop/errors])]
    (if (empty? errors)
      [:div.NoErrors]
      [:> mui/Container
       {:max-width :md}
       [muic/Stack
        {:items errors
         :template [Error]
         :style {:margin-bottom "1rem"}}]])))


(defn WorkareaSwitch []
  [muic/ErrorBoundary
   (<subscribe [:desktop/current-page-workarea])])


(defn ToolbarSwitch []
  [muic/ErrorBoundary
   (or (<subscribe [:desktop/current-page-toolbar])
       [:div])])


(defn AppBarToolbar []
  [:div.Toolbar
   {:style {:display :flex}}
   [muic/ErrorBoundary
    [ToolbarSwitch]]])


(defn TitleSwitch [fallback-title]
  (let [title (or (<subscribe [:desktop/current-page-title-text])
                  fallback-title)
        title (if (fn? title) (title) title)]
    (set! (. js/document -title) title)
    [:span.Title
     title]))


(defn Title [fallback-title]
  [:div
   {:style {:margin-left "1rem"}}
   [:> mui/Typography
    {:variant :h5
     :color :inherit}
    [TitleSwitch fallback-title]]])


(defn MainNavIconButtonSwitch [index-page-element]
  (if (= :index (<subscribe [:desktop/current-page-ident]))
    index-page-element
    [:> mui/IconButton
     {:color :inherit
      :on-click #( .back js/window.history)}
     [:> icons/ArrowBack]]))


(defn Snackbars []
  [:div.Snackbar
   (when-let [snackbar (<subscribe [:desktop/snackbar])]
     [:> mui/Snackbar
      {:open (-> snackbar :open?)
       :message (-> snackbar :message)}])])


(defn Desktop [{:keys [app-bar
                        container-max-width
                        footer
                        workarea-guard]}]
  [:div
   {:style {:font-family "\"Roboto\", \"Helvetica\", \"Arial\", sans-serif"
            :color "#333"}}
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider
    {:theme (theme/theme)}
    app-bar
    [:div
     {:style {:margin-top "84px"}}
     [Errors]
     [Snackbars]
     [:> mui/Container
      {:max-width container-max-width}
      [muic/ErrorBoundary
       (or workarea-guard
           [WorkareaSwitch])]]]]
   footer])


