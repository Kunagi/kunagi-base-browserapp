(ns kunagi-base-browserapp.modules.desktop.components
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [mui-commons.api :refer [<subscribe]]
   [mui-commons.components :as muic]
   [kunagi-base-browserapp.modules.desktop.api :as api]))


(defn Error [[msg info]]
  [muic/ErrorCard
   [:div
    {:style {:font-weight :bold}}
    msg]
   [muic/Data info]])


(defn Errors []
  (let [errors (<subscribe [:desktop/errors])]
    (if (empty? errors)
      [:div.NoErrors]
      [:> mui/Container
       {:max-width :md}
       [muic/Column
        {:items errors
         :template [Error]}]])))


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
