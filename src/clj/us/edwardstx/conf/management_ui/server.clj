(ns us.edwardstx.conf.management-ui.server
  (:require [yada.yada :as yada]
            [com.stuartsierra.component :as component]))

(defrecord Server [handler conf server]
  component/Lifecycle

  (start [this]
    (let [port (-> conf :conf :port)
          routes (-> handler :routes)
          s (yada/listener routes {:port port})]
      (do
        (println (str "Started server listening on port " port))
        (assoc this :server s))
      ))

  (stop [this]
    ((:close server))
    (println "Stopped server")
    (assoc this :server nil)))


(defn new-server []
  (component/using
   (map->Server {})
   [:handler :conf]))
