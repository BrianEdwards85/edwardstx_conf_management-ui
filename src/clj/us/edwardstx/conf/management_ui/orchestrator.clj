(ns us.edwardstx.conf.management-ui.orchestrator
  (:require [com.stuartsierra.component :as component]
            [us.edwardstx.conf.management-ui.data.conf :as conf]
            [us.edwardstx.common.uuid :refer [uuid]]
            [manifold.deferred :as d]
            [clojure.spec.alpha :as s]
            ))

(defrecord Orchestrator [db keys]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(s/def ::value ::conf/conf_value)
(s/def ::key ::conf/key_path)
(defn new-orchestrator []
  (component/using
   (map->Orchestrator {})
   [:db :keys]))

(defn get-keys [o]
  (conf/get-keys (:db o)))

(defn get-key-values [o k]
  (conf/get-key-values (:db o) k))

(defn get-services [o]
  (conf/get-services (:db o)))

(defn get-service-key-values [o service]
  (conf/get-service-key-values (:db o) service))

(defn insert-key [o k]
  (conf/insert-key (:db o) k))

(defn get-value-id [o data]
  {:pre [(s/valid? (s/keys :req-un [::key (or ::conf/id ::value)]))]}
  (if (:id data)
    (d/chain (conf/get-value-by-id (:db o) (:id data))
             #(if (= (:key data) (:key_path %1))
                (:id %1)
                (throw (Exception. (str (:id data) " is not for key " (:key data))))
                ))
    (let [new-id (uuid)]
      (d/chain (conf/insert-key-value (:db o)
                                      {:id new-id
                                       :key_path (:key data)
                                       :conf_value (:value data)})
               #(if (= 1 %) new-id (throw (Exception. "Unable to add key value")))))))

(defn set-service-key [o data]
  (d/chain (get-value-id o data)
           #(conf/set-service-key (:db o)
                                          {:service (:service data)
                                           :key_path (:key data)
                                           :id %1})
           ))
