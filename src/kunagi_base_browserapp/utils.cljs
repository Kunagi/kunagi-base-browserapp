(ns kunagi-base-browserapp.utils
  (:require
   [clojure.string :refer [split]]))


(defn parse-location-params
  "Parse URL parameters into a hashmap"
  []
  (let [param-strs (-> (.-location js/window) (split #"\?") last (split #"\&"))]
    (into {} (for [[k v] (map #(split % #"=") param-strs)]
               [(keyword k) v]))))
