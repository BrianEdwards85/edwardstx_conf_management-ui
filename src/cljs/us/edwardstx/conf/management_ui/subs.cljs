(ns us.edwardstx.conf.management-ui.subs
  (:require [re-frame.core :as re-frame]))

(defn reg-subs []

  (re-frame/reg-sub
   :user
   (fn [db _]
     (:user db)))

  (re-frame/reg-sub
   :page
   (fn [db _]
     (:page db)))

  (re-frame/reg-sub
   :route-params
   (fn [db _]
     (:route-params db)))

  )

