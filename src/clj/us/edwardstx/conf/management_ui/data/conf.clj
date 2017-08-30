(ns us.edwardstx.conf.management-ui.data.conf
  (:require [hugsql.core :as hugsql]
            [us.edwardstx.common.db :refer [get-connection] :as db]
            [us.edwardstx.common.spec :as specs]
            [clojure.spec.alpha :as s]
            [manifold.deferred :as d]))

(hugsql/def-db-fns "us/edwardstx/conf/management_ui/data/sql/conf.sql")

(def key-regex #"^[A-Za-z0-9]+(\.[A-Za-z0-9]+)*$")
(s/def ::key_path (s/and ::specs/non-empty-string #(re-matches key-regex %)))
(s/def ::secret boolean?)
(s/def ::parser (s/nilable ::specs/non-empty-string))
(s/def ::conf_value ::specs/non-empty-string)
(s/def ::id ::specs/uuid)
(s/def ::service ::specs/non-empty-string)

(defn get-keys [db]
  {:pre [(s/valid? ::db/db db)]}
  (d/future
    (get-keys-sql (get-connection db))))

(defn get-key-values [db k]
  {:pre [(s/valid? ::db/db db)
         (s/valid? ::specs/non-empty-string k)]}
  (d/future
    (get-key-values-sql (get-connection db) {:key k})))

(defn get-services [db]
  {:pre [(s/valid? ::db/db db)]}
  (d/future
    (map :service (get-services-sql (get-connection db)))
    ))

(defn get-service-key-values [db service]
  {:pre [(s/valid? ::db/db db)
         (s/valid? ::specs/non-empty-string service)]}
  (d/future
    (get-service-key-values-sql (get-connection db) {:service service})))

(defn insert-key [db v]
  {:pre [(s/valid? ::db/db db)
         (s/valid? (s/keys :req-un [::key_path] :opt-un [::secret ::parser]))]}
  (d/future
    (insert-key-sql (get-connection db) (merge {:secret false :parser nil} v))))

(defn insert-key-value [db kv]
  {:pre [(s/valid? ::db/db db)
         (s/valid? (s/keys :req-un [::key_path ::id ::conf_value]))]}
  (d/future
    (insert-key-value-sql (get-connection db) kv)))

(defn set-service-key [db skv]
  {:pre [(s/valid? ::db/db db)
         (s/valid? (s/keys :req-un [::key_path ::id ::service]))]}
  (d/future
    (set-service-key-value-sql (get-connection db) skv)))

(defn get-value-by-id [db id]
  {:pre [(s/valid? ::db/db db)
         (s/valid? ::specs/uuid id)]}
  (d/future
    (get-value-by-id-sql (get-connection db) {:id id})))
