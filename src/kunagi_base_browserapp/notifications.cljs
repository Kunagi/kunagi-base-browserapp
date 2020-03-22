(ns kunagi-base-browserapp.notifications)


(defn permission-granted? []
  (= "granted"
     (-> js/Notification .-permission)))


(defn request-permission [callback]
  (tap> [:inf ::request-permission])
  (-> js/Notification .requestPermission (.then callback)))


(defn show-notification [title options]
  (js/Notification. title (clj->js options)))
