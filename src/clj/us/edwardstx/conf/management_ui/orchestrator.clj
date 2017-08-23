(ns us.edwardstx.conf.management-ui.orchestrator
  (:require [com.stuartsierra.component :as component]
            [us.edwardstx.conf.management-ui.data.conf :as conf]
            ))

(defrecord Orchestrator [db]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(defn new-orchestrator []
  (component/using
   (map->Orchestrator {})
   [:db]))

(defn get-keys [o]
  (conf/get-keys (:db o)))

(defn get-key-values [o k]
  (conf/get-key-values (:db o) k))
