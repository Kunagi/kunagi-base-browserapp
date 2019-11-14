(ns kunagi-base-browserapp.modules.tracking.api
  (:require
   [reagent.core :as r]
   [kunagi-base-browserapp.google-analytics :as ga]))

(defonce !track (r/atom (fn [event-name event-params]
                          (js/console.log "TRACK" event-name event-params)
                          (apply ga/track event-name event-params))))


(defn track!
  ([event-name]
   (track! event-name nil))
  ([event-name event-params]
   (@!track event-name event-params)))


(defn track-screen-view! [screen-name params]
  (track! "screen_view" (assoc params
                               "screen_name"
                               (if (keyword? screen-name)
                                 (name screen-name)
                                 (str screen-name)))))
