(ns kunagi-base-browserapp.modules.devtools.model
  (:require
   [kunagi-base.appmodel :refer [def-module]]
   [kunagi-base.modules.startup.model :refer [def-init-function]]
   [kunagi-base-browserapp.modules.desktop.model :refer [def-page]]

   [kunagi-base-browserapp.modules.devtools.api :as impl]
   [kunagi-base-browserapp.modules.devtools.tap :as tap]))


(def-module
  {:module/id ::devtools})


(def-page
  {:page/id ::tap-page
   :page/ident :devtools-tap
   :page/module [:module/ident :devtools]
   :page/title-text "devtools: tap>"
   :page/workarea [tap/Workarea]})


(def-init-function
  {:init-function/id ::graphed
   :init-function/module [:module/ident :devtools]
   :init-function/f #(impl/init-graphed %)})


(def-init-function
  {:init-function/id ::tap
   :init-function/module [:module/ident :devtools]
   :init-function/f #(tap/init-tap! %)})
