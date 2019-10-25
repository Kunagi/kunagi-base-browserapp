(ns kunagi-base-browserapp.utils
  (:require
   [clojure.string :refer [split]]))


(defn scroll-to-top! []
  (js/window.scrollTo 0 0))


(defn parse-location-params
  "Parse URL parameters into a hashmap"
  []
  (let [param-strs (-> (.-location js/window) (split #"\?") last (split #"\&"))]
    (into {} (for [[k v] (map #(split % #"=") param-strs)]
               [(keyword k) v]))))
