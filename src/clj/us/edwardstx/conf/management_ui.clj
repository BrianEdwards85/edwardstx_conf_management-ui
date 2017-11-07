(ns us.edwardstx.conf.management-ui
  (:require [config.core :refer [env]]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [us.edwardstx.conf.management-ui.orchestrator :refer [new-orchestrator]]
            [us.edwardstx.conf.management-ui.handler :refer [new-handler]]
            [us.edwardstx.common.logging :refer [new-logging]]
            [us.edwardstx.common.rabbitmq :refer [new-rabbitmq]]
            [us.edwardstx.common.server :refer [new-server]]
            [us.edwardstx.common.tasks :refer [new-tasks]]
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
   :logging (new-logging)
   :conf (new-conf env)
   :db (new-database)
   :orchestrator (new-orchestrator)
   :handler (new-handler)
   :tasks (new-tasks)
   :rabbitmq (new-rabbitmq)
   :server (new-server)
   ))

(defn -main [& args]
  (let [semaphore (d/deferred)]
    (reset! system (init-system env))

    (swap! system component/start)
    (log/info "Management UI booted")
    (deref semaphore)
    (log/info "Management going down")
    (component/stop @system)

    (shutdown-agents)
    ))


(comment
  (use 'us.edwardstx.conf.management-ui :reload)

  (in-ns 'us.edwardstx.conf.management-ui)

  (reset! system (init-system env))

  (swap! system component/start)

  )


