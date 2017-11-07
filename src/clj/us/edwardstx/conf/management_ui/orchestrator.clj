(ns us.edwardstx.conf.management-ui.orchestrator
  (:require [com.stuartsierra.component :as component]
            [us.edwardstx.conf.management-ui.data.conf :as conf]
            [us.edwardstx.conf.management-ui.data.services :as services]
            [clojure.tools.logging :as log]
            [us.edwardstx.common.keys :as keys]
            [us.edwardstx.common.tasks :as tasks]
            [us.edwardstx.common.uuid :refer [uuid]]
            [manifold.deferred :as d]
            [clojure.spec.alpha :as s]
            ))

(defrecord Orchestrator [db keys tasks]
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
   [:db :keys :tasks]))

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
  {:pre [(s/valid? (s/keys :req-un [::key (or ::conf/id ::value)]) data)]}
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
           (fn [_] (get-service-key-values o (:service data)))))

(defn remove-service-key-value [o data]
  (d/chain
   (conf/remove-service-key-value (:db o) data)
   (fn [_] (get-service-key-values o (:service data)))))

(defn create-service-accounts [tasks opts]
  (tasks/send-task-with-response tasks "service-account-creation-service.account.create" opts))

(defn set-credentials [o type opts {:keys [passwd account]}]
  (if ((set (:types opts)) type)
    (d/zip
     (set-service-key o {:service account :value passwd :key (format "%s.password" type)})
     (set-service-key o {:service account :value account :key (format "%s.username" type)}))
    (d/success-deferred [])))

(defn insert-service [{:keys [db tasks] :as o} {:keys [service] :as opts}]
  (let [kp (keys/generate-key-pair)]
    (d/chain
     (services/insert-service db service (:public-key kp))
     (fn [_] (create-service-accounts tasks (assoc opts :account service)))
     (fn [{:keys [sucess error] :as cred}]
       (if sucess
         (d/zip
          (set-credentials o "db" opts cred)
          (set-credentials o "rabbit" opts cred))
         (let [msg (format "Unable to create service accounts for %s: %s" service error)]
           (log/error msg )
           (throw (Exception. msg)))))
     (fn [_]
       (assoc kp
              :conf-host "https://eight.edwardstx.us/conf"
              :auth-host "https://eight.edwardstx.us/auth"
              :service-name service))


    )))




