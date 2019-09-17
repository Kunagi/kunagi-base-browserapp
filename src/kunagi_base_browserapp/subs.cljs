(ns kunagi-base-browserapp.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :browserapp/config
 (fn [db _]
   (get db :browserapp/config)))
