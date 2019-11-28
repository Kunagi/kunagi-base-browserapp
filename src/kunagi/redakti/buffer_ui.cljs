(ns kunagi.redakti.buffer-ui
  (:require
   ["@material-ui/core" :as mui]
   [mui-commons.components :as muic]
   [mui-commons.theme :as theme]
   [kunagi.redakti.buffer :as buffer]))


(declare node)

(def spacing "5px")
(def palette
  {:cursor "#900"
   :node {:background-color "#222"}
   :container {:background-color "#111"}})
(def cursor-outline (str "1px solid " (-> palette :cursor)))
(def cursor-box-shadow "0px 2px 1px -1px rgba(200,0,0,0.2), 0px 1px 1px 0px rgba(200,0,0,0.14), 0px 1px 3px 0px rgba(200,0,0,0.12)")
(def cursor-background-color "#ddd")

(defn NodeFrame [options buffer n path component]
  [:> mui/Paper
   {:style {:padding spacing
            :background-color (when (= path (-> buffer :cursor)) cursor-background-color)}}

   ;;{:style {:outline (when (= path (-> buffer :cursor)) cursor-outline)}}
   ;; :background (-> palette :node :background-color)
   ;; :padding spacing}}
   component])


(defn Leaf [buffer n path]
  ;;[:div.Leaf]
  [NodeFrame {} buffer n path
   [:div
    (when-let [text (-> n :redakti.node/text)]
      [:div.Leaf-Text
       text])]])
    ;; [:div.Debug
    ;;  [muic/Data path]
    ;;  [muic/Data n]]])


(defn Column [buffer n path]
  [NodeFrame {} buffer n path
   [:div
    ;; [muic/Data path]
    (when-let [text (-> n :redakti.node/text)]
      [:div.Leaf-Text
       {:style {:padding-bottom spacing}}
       text])
    (into
     [muic/Stack
      {:spacing spacing
       :style {:padding spacing}}]
               ;;:background-color (-> palette :container :background-color)]
     (map-indexed
      (fn [idx _child-n]
        (node buffer (conj path idx)))
      (-> n :redakti.node/nodes)))]])


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
  [:div.Buffer
   {:style {:padding spacing}}
   (node buffer [])])
