(ns us.edwardstx.conf.management-ui.api
  (:require [us.edwardstx.conf.management-ui.api.v-one :as v1]
            [us.edwardstx.common.security :as auth]))


(defn build-routes [o]
  ["api/" [
           ["v1/" (v1/build-v1-routes o)]
           ["auth/" (auth/build-auth-routes (:keys o))]
           ]])

