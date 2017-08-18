(ns us.edwardstx.common.auth
  (:require [aleph.http :as http]
            [byte-streams :as bs]
            [com.stuartsierra.component :as component]
            [clj-crypto.core :as crypto]
            ))


(defrecord Auth [public-key env]
  component/Lifecycle

  (start [this]
    (assoc this :public-key
           (crypto/private-key-map {:algorithm "ECDSA"
                                    :bytes
                                    (-> env
                                        :auth-host
                                        (str "key")
                                        http/get
                                        deref
                                        :body
                                        bs/to-string
                                        crypto/decode-base64)})))

  (stop [this]
    (assoc this :public-key nil)))

(defn new-auth [env]
  (map->Auth {:env (select-keys env [:auth-host])}))

