(ns us.edwardstx.conf.management-ui.data.conf
  (:require [hugsql.core :as hugsql]
            [us.edwardstx.common.db :refer [get-connection]]
            [manifold.deferred :as d]))

(hugsql/def-db-fns "us/edwardstx/conf/management_ui/data/sql/conf.sql")

(defn get-keys [db]
  (d/future
    (get-keys-sql (get-connection db))))

(defb get-key-values [db k]
  (d/future
    (get-key-values-sql (get-connection db) {:key k})))
