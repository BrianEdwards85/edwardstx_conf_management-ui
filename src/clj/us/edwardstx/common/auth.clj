(ns us.edwardstx.common.auth
  (:require [aleph.http :as http]
            [byte-streams :as bs]
            [com.stuartsierra.component :as component]
            [clj-crypto.core :as crypto]

            [manifold.deferred :as d]))


(defrecord Auth [public-key env]
  component/Lifecycle

  (start [this]
    (assoc this :public-key
           (crypto/decode-private-key {:algorithm "ECDSA"
                                    :bytes
                                    (let [url (-> env :auth-host (str "/key"))]
                                      (-> url
                                          http/get
                                          (d/catch (fn [e]
                                                     (throw
                                                      (Exception.
                                                       (str "Unable to get auth key from " url " status: " (:status e))))))
                                          deref
                                          :body
                                          bs/to-string
                                          crypto/decode-base64))})))

  (stop [this]
    (assoc this :public-key nil)))

(defn new-auth [env]
  (map->Auth {:env (select-keys env [:auth-host])}))

