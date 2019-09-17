(ns kunagi-base-browserapp.devutils.components
  (:require
   [mui-commons.api :refer [<subscribe]]
   [mui-commons.components :as muic]))


(defn Aggregates []
  (let [aggregates (<subscribe [:event-sourcing/aggregates])]
    [muic/Data aggregates]))
