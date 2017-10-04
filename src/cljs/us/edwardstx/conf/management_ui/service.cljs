(ns us.edwardstx.conf.management-ui.service
  (:require [ajax.core :as ajax]))

(def json (ajax/json-response-format {:keywords? true}))

(defn user-info [valid-user no-user]
  {:http-xhrio {:method          :get
                :uri             "/management-ui/api/auth/user"
                :response-format json
                :on-success      [valid-user]
                :on-failure      [no-user]}})

