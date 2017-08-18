(ns ^:figwheel-no-load us.edwardstx.conf.management-ui.dev
  (:require
    [us.edwardstx.conf.management-ui.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
