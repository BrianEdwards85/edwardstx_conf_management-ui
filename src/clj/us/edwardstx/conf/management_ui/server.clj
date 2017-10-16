(ns us.edwardstx.conf.management-ui.server
  (:require [yada.yada :as yada]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))

(defrecord Server [handler conf server]
  component/Lifecycle

  (start [this]
    (let [port (-> conf :conf :port)
          routes (-> handler :routes)
          s (yada/listener routes {:port port})]
      (do
        (log/info (str "Started server listening on port " port))
        (assoc this :server s))
      ))

  (stop [this]
    ((:close server))
    (log/warn "Stopped server")
    (assoc this :server nil)))


(defn new-server []
  (component/using
   (map->Server {})
   [:handler :conf]))
