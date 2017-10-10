(ns ^:figwheel-no-load us.edwardstx.conf.management-ui.dev
  (:require
    [us.edwardstx.conf.management-ui.core :as core]
    [re-frisk.core :as re-frisk]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(re-frisk/enable-re-frisk!)

(core/init!)
