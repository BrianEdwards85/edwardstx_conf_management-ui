(ns us.edwardstx.common.token
  (:require [aleph.http :as http]
            [byte-streams :as bs]
            [manifold.deferred :as d]
            [tick.core :refer [minutes seconds]]
            [tick.schedule :refer [schedule start]]
            [tick.timeline :refer [timeline periodic-seq]]
            [tick.clock :refer [now clock-ticking-in-seconds]]
            [com.stuartsierra.component :as component]
            [us.edwardstx.common.keys :as keys]))

(defn create-timeline [s]
  (timeline (periodic-seq (now) (seconds s))))

(defn run-schedule [tl f]
  (let [sch (schedule f tl)]
    (start sch (clock-ticking-in-seconds))
    sch))

(defn get-token [token]
  (let [sst (keys/create-self-signed-token (:keys token))]
    (d/chain (http/post (str (-> token :env :auth-host)
                             "/api/service/"
                             (-> token :env :service-name)
                             "/token")
                        {:body sst})
             :body
             bs/to-string)))


(defrecord Token [keys token env]
  component/Lifecycle

  (start [this]
    (assoc this
           :token
           (atom @(get-token this))))

  (stop [this]
    (do
      (if token (reset! token nil))
      (assoc this :token nil)))
  )

(defn new-token [env]
  (component/using
   (map->Token {:env (select-keys env [:auth-host :service-name])})
   [:keys]))

