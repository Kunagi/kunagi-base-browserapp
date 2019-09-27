(ns kunagi-base-browserapp.modules.assets.localstorage)


(defn load-asset [asset asset-path]
  nil)


(defn on-asset-updated [asset asset-path value]
  (tap> [:!!! ::asset-updated asset asset-path value]))
