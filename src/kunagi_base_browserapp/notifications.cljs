(ns kunagi-base-browserapp.notifications)


(defn info []
  (if-not (exists? js/Notification)
    :not-supported
    {:permission (-> js/Notification .-permission)}))


(defn supported? []
  (exists? js/Notification))


(defn permission-granted? []
  (and (supported?)
       (= "granted" (-> js/Notification .-permission))))


(defn request-permission [callback]
  (tap> [:inf ::request-permission])
  (if-not (supported?)
    (tap> [:wrn ::request-permission :notifications-not-supported])
    (-> js/Notification
        .requestPermission
        (.then callback))))


(defn show-notification [title options]
  (if-not (supported?)
    (tap> [:wrn ::show-notification :notifications-not-supported])
    (try
      (js/Notification. title (clj->js options))
      (catch :default ex
        (tap> [:err ::show-notification ex])))))
