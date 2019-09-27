(ns kunagi-base-browserapp.modules.assets.localstorage
  (:require
   [cljs.reader :refer [read-string]]))


(defn set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) key val))

(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(defn remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) key))


(defn storage-key [asset-pool asset-path]
  (let [asset-pool-ident (-> asset-pool :asset-pool/ident)]
    (str "assets/asset "
         (namespace asset-pool-ident)
         " "
         (name asset-pool-ident)
         " "
         (-> asset-path))))


(defn load-asset [asset-pool asset-path]
  (when (-> asset-pool :asset-pool/localstorage?)
    (tap> [:!!! ::load-asset (storage-key asset-pool asset-path)])
    (when-let [s (get-item (storage-key asset-pool asset-path))]
      (tap> [:!!! ::load-asset s])
      (read-string s))))


(defn on-asset-updated [asset-pool asset-path value]
  (when (-> asset-pool :asset-pool/localstorage?)
    (tap> [:!!! ::storing asset-pool asset-path value])
    (set-item! (storage-key asset-pool asset-path) (pr-str value))))
