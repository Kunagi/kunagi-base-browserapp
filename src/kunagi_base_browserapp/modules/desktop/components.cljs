(ns kunagi-base-browserapp.modules.desktop.components
  (:require
   ["@material-ui/core" :as mui]

   [mui-commons.api :refer [<subscribe]]
   [mui-commons.components :as muic]
   [kunagi-base-browserapp.modules.desktop.api :as api]))


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
                  fallback-title)]
    (set! (. js/document -title) title)
    [:span.Title
     (if (fn? title) (title) title)]))


(defn Title [fallback-title]
  [:div
   {:style {:margin-left "1rem"}}
   [:> mui/Typography
    {:variant :h5
     :color :inherit}
    [TitleSwitch fallback-title]]])
