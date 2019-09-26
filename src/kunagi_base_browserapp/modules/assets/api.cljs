(ns kunagi-base-browserapp.modules.assets.api
  (:require
   [cljs.reader :as reader]
   [re-frame.core :as rf]
   [ajax.core :as ajax]

   [kunagi-base.appmodel :as am]))


(rf/reg-sub
 :assets/asset
 (fn [db [_ asset-pool-ident asset-path]]
   (get-in db [:assets/asset-pools asset-pool-ident asset-path])))


(rf/reg-event-db
 :assets/asset-received
 (fn [db [_ {:keys [asset-pool-ident
                    asset-path
                    data]}]]
   (tap> [:dbg ::asset-received asset-pool-ident asset-path])
   (assoc-in db [:assets/asset-pools asset-pool-ident asset-path] data)))


(rf/reg-event-db
 ::asset-request-failed
 (fn [db [_ {:keys [asset-pool-ident
                    asset-path
                    url
                    error]}]]
   (let [error (merge error
                      {:asset-pool-ident asset-pool-ident
                       :asset-path asset-path
                       :url url})]
     (tap> [:wrn ::asset-request-failed error])
     (assoc-in db
               [:assets/asset-pools asset-pool-ident asset-path]
               [:resource/error error]))))


(defn- ajax-error-handler [asset-pool-ident asset-path url]
  (fn [error]
    (rf/dispatch
     [::asset-request-failed
      {:asset-pool-ident asset-pool-ident
       :asset-path asset-path
       :url url
       :error error}])))


(defn- ajax-success-handler [asset-pool-ident asset-path]
  (fn [response]
    (rf/dispatch
     [:assets/asset-received
      {:asset-pool-ident asset-pool-ident
       :asset-path asset-path
       :data (reader/read-string response)}])))


(defn- ajax-url [asset-pool asset-path]
  (if-let [url-path (-> asset-pool :asset-pool/url-path)]
    (str "/" url-path "/" asset-path)
    (str "/api/asset?edn="
         (pr-str [(-> asset-pool :asset-pool/ident) asset-path]))))


(defn- request-asset-via-ajax! [asset-pool asset-path]
  (let [asset-pool-ident (-> asset-pool :asset-pool/ident)
        url (ajax-url asset-pool asset-path)]
    (ajax/GET url
              {:handler (ajax-success-handler asset-pool-ident asset-path)
               :error-handler (ajax-error-handler asset-pool-ident asset-path url)})))


(defn- request-asset-via-comm-async! [asset-pool asset-path]
  (let [asset-pool-ident (-> asset-pool :asset-pool/ident)]
    (rf/dispatch [:comm-async/send-event [:assets/asset-requested [asset-pool-ident asset-path]]])))


(defn- request-asset-from-pool! [asset-pool asset-path]
  (if (-> asset-pool :asset-pool/url-path)
    (request-asset-via-ajax! asset-pool asset-path)
    (request-asset-via-comm-async! asset-pool asset-path)))


(defn- request-startup-assets-from-pool
  [asset-pool]
  (doseq [asset-path (-> asset-pool :asset-pool/request-on-startup)]
    (request-asset-from-pool! asset-pool asset-path)))


(defn- q-asset-pools-with-request-on-startup []
  '[:find ?e
    :where
    [?e :asset-pool/request-on-startup _]])


(defn request-startup-assets [app-db]
  (doseq [[asset-pool-id] (am/q! (q-asset-pools-with-request-on-startup))]
    (request-startup-assets-from-pool (am/entity! asset-pool-id)))
  app-db)


(defn request-asset! [asset-ident asset-path]
  (request-asset-from-pool! (am/entity! [:asset-pool/ident asset-ident]) asset-path))


(defn reg-sub-for-asset-pool [asset-pool]
  (let [asset-pool-ident (-> asset-pool :asset-pool/ident)]
    (rf/reg-sub
     asset-pool-ident
     (fn [db [_ asset-path]]
       (get-in db [:assets/asset-pools asset-pool-ident asset-path])))))
