(ns us.edwardstx.conf.management-ui
  (:require [config.core :refer [env]]
            [com.stuartsierra.component :as component]
            [us.edwardstx.common.conf :refer [new-conf]]
            [us.edwardstx.common.token :refer [new-token]]
            [us.edwardstx.common.keys :refer [new-keys]]
            [us.edwardstx.common.db :refer [new-database]])

  (:gen-class))

(defonce system (atom {}))

(defn init-system [env]
  (component/system-map
   :keys (new-keys env)
   :token (new-token env)
   :conf (new-conf env)
   :database (new-database)
   ))

(defn -main [& args]
  (reset! system (init-system env)))


(comment
  (use 'us.edwardstx.conf.management-ui :reload)

  (in-ns 'us.edwardstx.conf.management-ui)

  (reset! system (init-system env))

  (swap! system component/start)

  )


