(ns kunagi-base-browserapp.modules.desktop.api
  (:require
   [re-frame.core :as rf]
   [kunagi-base.appmodel :as am]))


(defn page [page-ident]
  (when page-ident
    (am/entity! [:page/ident page-ident])))


(defn activate-page [db page-ident]
  ;; TODO action listeners
  (assoc db :desktop/current-page-ident page-ident))


(rf/reg-sub
 :desktop/current-page-ident
 (fn [db]
   (or
    (get db :desktop/current-page-ident)
    :index)))


(rf/reg-sub
 :desktop/current-page-workarea
 (fn [_]
   (rf/subscribe [:desktop/current-page-ident]))
 (fn [page-ident _]
   (if-let [page (page page-ident)]
     (-> page :page/workarea)
     [:div "Page not found " (pr-str page-ident)])))


(rf/reg-sub
 :desktop/current-page-toolbar
 (fn [_]
   (rf/subscribe [:desktop/current-page-ident]))
 (fn [page-ident _]
   (if-let [page (page page-ident)]
     (-> page :page/toolbar))))


(rf/reg-sub
 :desktop/current-page-title-text
 (fn [_]
   (rf/subscribe [:desktop/current-page-ident]))
 (fn [page-ident _]
   (if-let [page (page page-ident)]
     (-> page :page/title-text))))


(rf/reg-event-db
 :desktop/activate-page
 (fn [db [_ page-ident]]
   (activate-page db page-ident)))
