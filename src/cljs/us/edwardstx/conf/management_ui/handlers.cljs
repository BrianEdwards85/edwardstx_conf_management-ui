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

  (re-frame/reg-event-fx
   :get-key-values
   (fn [{:keys [db]} [_ key]]
     (merge
      (service/get-key-values key :get-key-values-success :get-failure)
      {:db (assoc db :key-values nil)})))

  (re-frame/reg-event-db
   :get-key-values-success
   (fn [db [_ key r]]
     (assoc db
            :key-values r
            :service-key-edit key)))

  (re-frame/reg-event-fx
   :get-service-keys
   (fn [_ [_ service]]
     (service/get-service-keys service :get-service-keys-success :get-failure)))

  (re-frame/reg-event-db
   :get-service-keys-success
   (fn [db [_ r]]
     (assoc db
            :service-keys r
            :key-values nil
            :service-key-edit nil
            )))

  (re-frame/reg-event-fx
   :get-services
   (fn [_ _]
     (service/get-services :get-services-success :get-failure)))

  (re-frame/reg-event-db
   :get-services-success
   (fn [db [_ r]]
     (assoc db :services r)))

  (re-frame/reg-event-db
   :get-failure
   (fn [db r]
     (assoc db :error {:raw r :desc "Oh no!"})))

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
