(ns us.edwardstx.conf.management-ui
  (:require [config.core :refer [env]]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [us.edwardstx.conf.management-ui.orchestrator :refer [new-orchestrator]]
            [us.edwardstx.conf.management-ui.handler :refer [new-handler]]
            [us.edwardstx.conf.management-ui.server :refer [new-server]]
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
   :db (new-database)
   :orchestrator (new-orchestrator)
   :handler (new-handler)
   :server (new-server)
   ))

(defn -main [& args]
  (let [semaphore (d/deferred)]
    (reset! system (init-system env))

    (swap! system component/start)

    (deref semaphore)

    (component/stop @system)

    (shutdown-agents)
    ))


(comment
  (use 'us.edwardstx.conf.management-ui :reload)

  (in-ns 'us.edwardstx.conf.management-ui)

  (reset! system (init-system env))

  (swap! system component/start)

  )


