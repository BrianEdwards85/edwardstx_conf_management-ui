(ns us.edwardstx.conf.management-ui.data.conf
  (:require [hugsql.core :as hugsql]
            [manifold.deferred :as d]))

(hugsql/def-db-fns "us/edwardstx/conf/management_ui/data/sql/conf.sql")

