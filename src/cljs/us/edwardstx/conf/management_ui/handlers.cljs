(ns us.edwardstx.conf.management-ui.handlers
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [us.edwardstx.conf.management-ui.service :as service]
            [us.edwardstx.conf.management-ui.routes :as routes]))

(def init-db
  {:page ::routes/root
   :route-params nil
   :user nil})

(defn reg-handlers []

  (re-frame/reg-event-fx
   :initialize
   (fn [_ _]
     (merge
      (service/user-info :valid-user :no-user)
      {:db init-db})))

  (re-frame/reg-event-db
   :valid-user
   (fn [db [_ r]]
     (assoc db :user r)))

  (re-frame/reg-event-db
   :no-user
   (fn [db [_ _]]
     (assoc db :user nil)))

  (re-frame/reg-event-db
   :nav
   (fn [db [_ {:keys [handler route-params]}]]
     (assoc db :page handler :route-params route-params)))

  )