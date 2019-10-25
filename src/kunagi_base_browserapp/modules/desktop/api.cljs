(ns kunagi-base-browserapp.modules.desktop.api
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [re-frame.core :as rf]
   [accountant.core :as accountant]
   [cemerick.url :refer (url url-encode)]

   [kunagi-base.utils :as utils]
   [kunagi-base.appmodel :as am]
   [kunagi-base-browserapp.utils :refer [parse-location-params
                                         scroll-to-top!]]))


(s/def :desktop/page-ident simple-keyword?)
(s/def :desktop/page-args map?)


(defn- parse-location []
  (let [pathname (.. js/document -location -pathname)
        page (.substring pathname 4)
        page (if (empty? page) "index" page)
        page-ident (keyword page)
        page-args (parse-location-params)]
    [page-ident page-args]))


(defn page [page-ident]
  (when page-ident
    (am/entity! [:page/ident page-ident])))


(defn navigate! [page-ident page-args]
  (accountant/navigate!
   (str "/ui/"
        (when-not (= :index page-ident)
          (url-encode (name page-ident)))
        (when-not (empty? page-args)
          (reduce
           (fn [s [k v]]
             (str s
                  (if (empty? s) "?" "&")
                  (url-encode (name k))
                  "="
                  (url-encode v)))
           ""
           page-args)))))



(defn- activate-page
  [db page-ident page-args]
  (utils/assert-spec :desktop/page-ident page-ident ::activate-page.page-ident)
  (utils/assert-spec :desktop/page-args page-args ::activate-page.page-args)
  (let [current-page (get db :desktop/current-page-ident)
        current-args (get-in db [:desktop/pages-args current-page])]
    (if (and (= page-ident current-page)
             (= page-args current-args))
      db
      (do
        (tap> [:dbg ::activate-page page-ident page-args])
        (rf/dispatch [:tracking/screen-view page-ident {:view-args page-args}])
        (scroll-to-top!)
        (let [page (am/entity! [:page/ident page-ident])
              on-activate-f (or (-> page :page/on-activate-f)
                                (fn [db page-args] db))
              loc (parse-location)]
          (when-not (= [page-ident page-args] (parse-location))
            (navigate! page-ident page-args))
          (-> db
              (assoc :desktop/current-page-ident page-ident)
              (assoc-in [:desktop/pages-args page-ident] page-args)
              (on-activate-f page-args)))))))



(defn install-error-handler []
  (set! (.-onerror js/window)
        (fn [msg url line col error]
          (let [error-info {:msg msg
                            :url url
                            :line line
                            :col col
                            :error error}]
            (tap> [:err ::error error-info])
            (rf/dispatch [:desktop/error "JavaScript Error" error-info]))
          true)))


(rf/reg-sub
 :desktop/errors
 (fn [db]
   (get db :desktop/errors)))


(rf/reg-event-db
 :desktop/error
 (fn [db [_ msg info]]
   (update db :desktop/errors conj [msg info])))


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
 :desktop/page-args
 (fn [_]
   (rf/subscribe [:desktop/pages-args]))
 (fn [pages-args [_ page-ident]]
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



(defn- accountant-nav-handler [path]
  (let [[page-ident page-args] (parse-location)]
    (rf/dispatch [:desktop/activate-page page-ident page-args])))


(defn install-accountant! []
  (accountant/configure-navigation!
   {:nav-handler accountant-nav-handler
    :path-exists? (fn [path] (str/starts-with? path "/ui/"))
    :reload-same-path? false})
  (accountant/dispatch-current!))

