(ns kunagi-base-browserapp.modules.desktop.model
  (:require
   [kunagi-base.appmodel :as am :refer [def-module def-entity-model]]

   [kunagi-base-browserapp.modules.desktop.components]))


(def-module
  {:module/id ::desktop})


(def-entity-model
  :desktop ::page
  {:page/ident {:uid? true :spec simple-keyword?}
   :page/workarea {:req true}
   :page/toolbar {}
   :page/title-text {}})


(defn def-page [page]
  (am/register-entity :page page))
