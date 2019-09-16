(ns kunagi-base-browserapp.http-async
  (:require
   [re-frame.core :as rf]
   [taoensso.sente  :as sente]

   [kunagi-base.appmodel :refer [def-module]]
   [kunagi-base.startup :refer [def-init-function]]))


(def-module
  {:module/id ::http-async})


(defn- connect []
  (tap> [:dbg ::connect])
  (let [{:keys [chsk ch-recv send-fn state] :as socket}
        (sente/make-channel-socket-client! "/chsk" {:type :auto})]
    (add-watch state :state->db (fn [_ _ _ state] (rf/dispatch [::state-changed state])))
    (sente/start-client-chsk-router! ch-recv #(rf/dispatch [::data-received %]))
    socket))



(def-init-function
  {:init-function/id ::connect
   :init-function/f #(assoc % :http-async/sente-socket (connect))})


(defn- on-event-received [db event]
  (rf/dispatch event)
  db)

(defn- on-data-received [db data]
  ;; (tap> [:!!! ::data-received data])
  (if (= :chsk/recv (-> data :id))
    (let [event (-> data :event (second))
          event-id (first event)]
      (if (= :kunagi-base/event event-id)
        (on-event-received db (second event))
        db)
      db)
    db))


(rf/reg-event-db
 ::data-received
 (fn [db [_ data]]
   (on-data-received db data)))


(rf/reg-event-db
 ::state-changed
 (fn [db [_ state]]
   (tap> [:dbg ::sente-state-changed state])
   (-> db
       (assoc-in [:http-async/sente-state] state))))


(defn- send! [db message]
  (if (get-in db [:http-async/sente-state :open?])
    (let [send-fn (get-in db [:http-async/sente-socket :send-fn])]
      (send-fn message))
    (.setTimeout js/window #(rf/dispatch [:http-async/send message]) 1000)))


(rf/reg-event-db
 :http-async/send
 (fn [db [_ message]]
   (send! db message)
   db))


(rf/reg-event-db
 :http-async/send-event
 (fn [db [_ event]]
   (send! db [:kunagi-base/event event])
   db))

(rf/reg-sub
 :http-async/state
 (fn [db _]
   (get db :http-async/sente-state)))
