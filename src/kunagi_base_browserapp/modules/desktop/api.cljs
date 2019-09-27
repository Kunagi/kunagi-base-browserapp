(ns kunagi-base-browserapp.modules.desktop.api
  (:require
   [re-frame.core :as rf]
   [kunagi-base.appmodel :as am]))


(defn page [page-ident]
  (when page-ident
    (am/entity! [:page/ident page-ident])))


(defn activate-page
  [db page-ident page-args]
  (let [page (am/entity! [:page/ident page-ident])
        on-activate-f (or (-> page :page/on-activate-f)
                          (fn [db page-args] db))]
    (-> db
        (assoc :desktop/current-page-ident page-ident)
        (assoc-in [:desktop/pages-args page-ident] page-args)
        (on-activate-f page-args))))


(rf/reg-sub
 :desktop/current-page-ident
 (fn [db]
   (or
    (get db :desktop/current-page-ident)
    :index)))


(rf/reg-sub
 :desktop/pages-args
 (fn [db]
   (get db :desktop/pages-args)))


(rf/reg-sub
 :desktop/current-page-args
 (fn [_]
   [(rf/subscribe [:desktop/current-page-ident])
    (rf/subscribe [:desktop/pages-args])])
 (fn [[page-ident pages-args]]
   (get pages-args page-ident)))


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
 (fn [db [_ page-ident page-args]]
   (activate-page db page-ident page-args)))
