(ns kunagi-base-browserapp.devtools.model
  (:require
   [kunagi-base.appmodel :refer [def-module]]
   [kunagi-base.modules.startup.model :refer [def-init-function]]

   [kunagi-base-browserapp.modules.devtools.api :as impl]))


(def-module
  {:module/id ::devtools})


(def-init-function
  {:init-function/id ::graphed
   :init-function/module [:module/ident :devtools]
   :init-function/f #(impl/init-graphed %)})
