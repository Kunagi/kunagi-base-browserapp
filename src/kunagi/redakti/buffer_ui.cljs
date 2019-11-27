(ns kunagi.redakti.buffer-ui
  (:require
   ["@material-ui/core" :as mui]
   [mui-commons.components :as muic]
   [kunagi.redakti.buffer :as buffer]))


(declare node)


(def cursor-outline "2px solid red")


(defn Leaf [buffer n path]
  [:div.Leaf
   {:style {:background "#ff9"
            :padding "1em"
            :outline (when (= path (-> buffer :cursor)) cursor-outline)}}
   (when-let [text (-> n :redakti.node/text)]
     [:div.Leaf-Text
      text])])
   ;; [:div.Debug
   ;;  [muic/Data path]
   ;;  [muic/Data n]]])


(defn Column [buffer n path]
  [:div
   {:style {:border "1px solid blue"
            :padding "1em"
            :outline (when (= path (-> buffer :cursor)) cursor-outline)}}
   ;; [muic/Data path]
   (when-let [text (-> n :redakti.node/text)]
     [:div.Leaf-Text
      text])
   (into
    [muic/Stack]
    (map-indexed
     (fn [idx _child-n]
       (node buffer (conj path idx)))
     (-> n :redakti.node/nodes)))])


(defn Row [buffer n path]
  [:div "row"])


(defn node [buffer path]
  (let [tree (-> buffer :tree)
        n (buffer/node-by-path tree path)]
    (case (-> n :redakti.node/type)
      :column [Column buffer n path]
      :row [Row buffer n path]
      [Leaf buffer n path])))


(defn Buffer [buffer]
   (node buffer []))
