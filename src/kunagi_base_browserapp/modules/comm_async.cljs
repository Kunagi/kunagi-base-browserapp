(ns kunagi-base-browserapp.modules.comm-async
  (:require
   [kunagi-base.appmodel :refer [def-module]]
   [kunagi-base.modules.startup :refer [def-init-function]]

   [kunagi-base-browserapp.modules.comm-async.api :as impl]))


(def-module
  {:module/id ::comm-async})


(def-init-function
  {:init-function/id ::connect
   :init-function/module [:module/ident :comm-async]
   :init-function/f impl/start})
