(ns us.edwardstx.common.conf
  (:require [clojure.data.json :as json]
            [us.edwardstx.common.keys :as keys]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [byte-streams :as bs]
            [aleph.http :as http]))

(defn get-conf [host service token key]
  (d/chain
   (http/post (str host "/api/v1/conf/" service) {:body token})
   :body
   bs/to-string
   #(keys/decrypt key %)
   #(json/read-str % :key-fn keyword)))


(defrecord Conf [keys token conf env]
    component/Lifecycle

  (start [this]
    (assoc this :conf
           (merge env
                  @(get-conf (:conf-host env)
                             (:service-name env)
                             @(:token token)
                             keys))))

  (stop [this]
    (assoc this :conf nil)))

(defn new-conf [env]
  (component/using
   (map->Conf {:env (select-keys env [:port :auth-host :conf-host :service-name])})
   [:keys :token]))

