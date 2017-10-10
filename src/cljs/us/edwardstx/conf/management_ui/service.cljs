(ns us.edwardstx.conf.management-ui.service
  (:require [ajax.core :as ajax]))

(def json (ajax/json-response-format {:keywords? true}))

(defn user-info [valid-user no-user]
  {:http-xhrio {:method          :get
                :uri             "/management-ui/api/auth/user"
                :response-format json
                :on-success      [valid-user]
                :on-failure      [no-user]}})

(defn get-services [on-success on-failure]
  {:http-xhrio {:method          :get
                :uri             "/management-ui/api/v1/services"
                :response-format json
                :on-success      [on-success]
                :on-failure      [on-failure]}})

(defn get-service-keys [service on-success on-failure]
  {:http-xhrio {:method          :get
                :uri             (str "/management-ui/api/v1/services/" service)
                :response-format json
                :on-success      [on-success]
                :on-failure      [on-failure]}})

