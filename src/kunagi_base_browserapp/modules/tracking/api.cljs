(ns kunagi-base-browserapp.modules.tracking.api
  (:require
   [reagent.core :as r]))


(defonce !track (r/atom (fn [& args] (js/console.log "TRACK" args))))


(defn track! [event-name event-params]
  (@!track event-name event-params))


(defn track-screen-view! [screen-name params]
  (track! "screen_view" (assoc params
                               "screen_name"
                               (if (keyword? screen-name)
                                 (name screen-name)
                                 (str screen-name)))))
