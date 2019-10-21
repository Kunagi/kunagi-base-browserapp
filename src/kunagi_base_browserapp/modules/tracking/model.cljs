(ns kunagi-base-browserapp.modules.tracking.model
  (:require
   [clojure.spec.alpha :as s]

   [kunagi-base.appmodel :as am :refer [def-module def-entity-model]]
   [kunagi-base.modules.startup.model :refer [def-init-function]]

   [kunagi-base-browserapp.modules.tracking.rf-events]))


(def-module
 {:module/id ::tracking})
