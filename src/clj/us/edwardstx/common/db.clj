(ns us.edwardstx.common.db
  (:require [hikari-cp.core :refer :all]
            [clojure.spec.alpha :as s]
            [com.stuartsierra.component :as component]))

(defn create-db-conf [d]
  (assoc (select-keys d [:classname :subprotocol :password :username])
         :jdbc-url (str "jdbc:"
                        (:subprotocol d)
                        "://"
                        (:host d)
                        ":"
                        (:port d)
                        "/"
                        (:database d)
                        (-> d :jdbc :options))))


(defprotocol DBConnection
  (get-connection [this]))

(s/def ::db #(satisfies? DBConnection %))

(defrecord Database [conf datasource]
  component/Lifecycle

  (start [this]
    (let [db-conf (-> conf :conf :db create-db-conf)]
      (let [ds (make-datasource db-conf)]
        (assoc this :datasource ds))))

  (stop [this]
    (close-datasource datasource)
    (assoc this :datasource nil))

  DBConnection

  (get-connection [this] (select-keys this [:datasource]))
  )

(defn new-database []
  (component/using
   (map->Database {})
   [:conf]))
