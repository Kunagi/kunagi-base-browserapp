(ns kunagi-base-browserapp.notifications)


(defn info []
  (if-not (exists? js/Notification)
    :not-supported
    {:permission (-> js/Notification .-permission)}))


(defn supported? []
  (exists? js/Notification))


(defn permission-granted? []
  (= "granted"
     (-> js/Notification .-permission)))


(defn request-permission [callback]
  (tap> [:inf ::request-permission])
  (-> js/Notification
      .requestPermission
      (.then callback)))


(defn show-notification [title options]
  (try
    (js/Notification. title (clj->js options))
    (catch :default ex
      (tap> [:err ::show-notification ex]))))
