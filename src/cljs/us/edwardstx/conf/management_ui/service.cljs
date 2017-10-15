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

(defn get-key-values [key on-success on-failure]
  {:http-xhrio {:method          :get
                :uri             (str "/management-ui/api/v1/keys/" key)
                :response-format json
                :on-success      [on-success key]
                :on-failure      [on-failure]}})

(defn set-service-key [service kv on-success on-failure]
  (let [body (.stringify js/JSON (clj->js kv))]
    {:http-xhrio {:method          :post
                  :uri             (str "/management-ui/api/v1/services/" service)
                  :body            body
                  :response-format json
                  :format          (ajax/json-request-format)
                  :headers         {:content-type "application/json"}
                  :on-success      [on-success]
                  :on-failure      [on-failure]}}))

(defn get-keys [on-success on-failure]
  {:http-xhrio {:method          :get
                :uri             "/management-ui/api/v1/keys"
                :response-format json
                :on-success      [on-success]
                :on-failure      [on-failure]}})
