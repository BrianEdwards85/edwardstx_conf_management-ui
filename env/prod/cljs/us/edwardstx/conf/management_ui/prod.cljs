(ns us.edwardstx.conf.management-ui.prod
  (:require [us.edwardstx.conf.management-ui.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
