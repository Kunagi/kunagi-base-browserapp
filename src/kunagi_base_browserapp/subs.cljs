(ns kunagi-base-browserapp.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :appconfig/config
 (fn [db _]
   (get db :appconfig/config)))
