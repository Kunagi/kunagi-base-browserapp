(ns kunagi-base-browserapp.modules.assets.api
  (:require
   [cljs.reader :as reader]
   [re-frame.core :as rf]
   [ajax.core :as ajax]

   [kunagi-base.auth.api :as auth]
   [kunagi-base.context :as context]
   [kunagi-base.appmodel :as am]
   [kunagi-base-browserapp.modules.assets.localstorage :as localstorage]))

;;;

(defn- on-asset-updated [asset-pool-ident asset-path asset]
  (localstorage/on-asset-updated asset-pool-ident asset-path asset))


;;; api

(defn asset [db asset asset-path]
  (get-in db [:assets/asset-pools asset asset-path]))


(defn set-asset [db asset-pool-ident asset-path asset]
  ;; TODO assert spec of asset
  (let [old-asset (asset asset-pool-ident asset-path)]
    (if (= old-asset asset)
      db
      (let [asset (am/entity! [:asset-pool-ident asset-pool-ident])]
        (on-asset-updated asset asset-path asset)
        (assoc-in db [:assets/asset-pools asset-pool-ident asset-path] asset)))))


(defn update-asset [db asset-pool-ident asset-path update-f]
  (if-let [asset (asset db asset-pool-ident asset-path)]
    (set-asset db asset-pool-ident asset-path (update-f asset))
    (throw (ex-info (str "Asset "
                         (pr-str [asset-pool-ident asset-path])
                         " is not loaded. Updating failed.")
                    {:asset-pool-ident asset-pool-ident
                     :asset-path asset-path}))))


;;; re-frame

(rf/reg-sub
 :assets/asset
 (fn [db [_ asset-pool-ident asset-path]]
   (asset db asset-pool-ident asset-path)))


(rf/reg-event-db
 :assets/asset-received
 (fn [db [_ {:keys [asset-pool-ident
                    asset-path
                    data]}]]
   (tap> [:dbg ::asset-received asset-pool-ident asset-path])
   (set-asset db asset-pool-ident asset-path data)))


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
     ;; TODO display error to user
     db)))


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
    (rf/dispatch [:comm-async/send-event
                  [:assets/asset-requested asset-pool-ident asset-path]])))


(defn- request-asset-from-pool! [asset-pool asset-path]
  (if (-> asset-pool :asset-pool/url-path)
    (request-asset-via-ajax! asset-pool asset-path)
    (request-asset-via-comm-async! asset-pool asset-path)))


(defn- request-startup-assets-from-pool
  [asset-pool context]
  (let [req-perms (-> asset-pool :asset-pool/req-perms)]
    (if (auth/context-has-permissions? context req-perms)
      (doseq [asset-path (-> asset-pool :asset-pool/request-on-startup)]
        (request-asset-from-pool! asset-pool asset-path)))))


(defn- q-asset-pools-with-request-on-startup []
  '[:find ?e
    :where
    [?e :asset-pool/request-on-startup _]])


(defn request-startup-assets [app-db]
  (doseq [[asset-pool-id] (am/q! (q-asset-pools-with-request-on-startup))]
    (request-startup-assets-from-pool (am/entity! asset-pool-id)
                                      (context/from-rf-db app-db)))
  app-db)


(defn request-asset! [asset-ident asset-path]
  (request-asset-from-pool! (am/entity! [:asset-pool/ident asset-ident]) asset-path))


(defn reg-sub-for-asset-pool [asset-pool]
  (let [asset-pool-ident (-> asset-pool :asset-pool/ident)]
    (rf/reg-sub
     asset-pool-ident
     (fn [db [_ asset-path]]
       (asset db asset-pool-ident asset-path)))))

