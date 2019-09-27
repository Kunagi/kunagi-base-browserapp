(ns kunagi-base-browserapp.modules.assets.localstorage)


(defn on-asset-updated [asset asset-path asset]
  (tap> [:!!! ::asset-updated asset asset-path]))
