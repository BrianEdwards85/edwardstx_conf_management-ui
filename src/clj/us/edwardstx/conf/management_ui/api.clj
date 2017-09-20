(ns us.edwardstx.conf.management-ui.api
  (:require [us.edwardstx.conf.management-ui.api.v-one :as v1]))


(defn build-routes [o]
  ["api/" [
           ["v1/" (v1/build-v1-routes o)]
           ]])

