(ns kunagi.redakti
  (:require
   [clojure.spec.alpha :as s]
   [kunagi.redakti.buffer :as buffer]))


(s/def ::identifier #(= % ::identifier))
(s/def ::redakti (s/keys :req [::identifier]))


(defn redakti? [redakti]
  (s/valid? ::redakti redakti))


(defn reg-action [redakti action]
  (assoc-in redakti [:actions (-> action :ident)] action))


(defn map-key [redakti key action]
  (assoc-in redakti [:keymap key] action))


(defn !message [redakti message-type message]
  (assoc redakti :message [message-type message]))




(defn- reg-action--cursor-step-in [redakti]
  (reg-action
   redakti
   {:ident :cursor-step-in
    :f (fn [redakti] (update redakti :buffer buffer/!cursor-step-in))}))

(defn- reg-action--cursor-step-out [redakti]
  (reg-action
   redakti
   {:ident :cursor-step-out
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-step-out))}))

(defn- reg-action--cursor-next [redakti]
  (reg-action
   redakti
   {:ident :cursor-next
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-next))}))

(defn- reg-action--cursor-prev [redakti]
  (reg-action
   redakti
   {:ident :cursor-prev
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-prev))}))

(defn- reg-action--cursor-up [redakti]
  (reg-action
   redakti
   {:ident :cursor-up
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-up))}))

(defn- reg-action--cursor-down [redakti]
  (reg-action
   redakti
   {:ident :cursor-down
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-down))}))

(defn- reg-action--cursor-left [redakti]
  (reg-action
   redakti
   {:ident :cursor-left
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-left))}))

(defn- reg-action--cursor-right [redakti]
  (reg-action
   redakti
   {:ident :cursor-right
    :f     (fn [redakti] (update redakti :buffer buffer/!cursor-right))}))


(defn new-redakti [buffer]
  (-> {::identifier ::identifier
       :buffer buffer
       :actions {}
       :keymap {}}
      reg-action--cursor-step-in
      reg-action--cursor-step-out
      reg-action--cursor-prev
      reg-action--cursor-next
      reg-action--cursor-up
      reg-action--cursor-down
      reg-action--cursor-left
      reg-action--cursor-right
      (map-key "h" :cursor-left)
      (map-key "j" :cursor-down)
      (map-key "k" :cursor-up)
      (map-key "l" :cursor-right)
      (map-key "Enter" :enter)
      (map-key " " :menu)))


(defn !action [redakti action-ident]
  (let [redakti (dissoc redakti :message)]
    (if-let [action (-> redakti :actions (get action-ident))]
      (if-let [f (-> action :f)]
        (let [updated-redakti (f redakti)]
          (if (redakti? updated-redakti)
            updated-redakti
            (!message redakti :err (str "Action " action-ident " corrupted state!"))))
        (!message redakti :err (str "Missing :f in action " action-ident)))
      (!message redakti :inf (str "No action " action-ident)))))


(defn !action-for-key [redakti key]
  (if-let [action-ident (-> redakti :keymap (get key))]
    (!action redakti action-ident)
    (!message redakti :inf (str "No action associated with key [" key "]"))))
