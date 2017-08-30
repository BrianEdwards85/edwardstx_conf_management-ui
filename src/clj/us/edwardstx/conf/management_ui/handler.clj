(ns us.edwardstx.conf.management-ui.handler
  (:require [com.stuartsierra.component :as component]
            [us.edwardstx.conf.management-ui.orchestrator :as orchestrator]
            [us.edwardstx.conf.management-ui.api :as api]
            [us.edwardstx.common.security :refer [authorized]]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [yada.yada :as yada]))

(defn build-routes [o]
  ["/management-ui/" [
                      (api/build-routes o)
                      ]])

(defrecord Handler [orchestrator routes]
  component/Lifecycle

  (start [this]
    (assoc this :routes (build-routes orchestrator)))

  (stop [this]
    (assoc this :routes nil)))


(defn new-handler []
  (component/using
   (map->Handler {})
   [:orchestrator]))
