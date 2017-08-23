(ns us.edwardstx.common.keys
    (:require [clj-crypto.core :as crypto]
              [aleph.http :as http]
              [manifold.deferred :as d]
              [byte-streams :as bs]
              [buddy.sign.jwt :as jwt]
              [clj-time.core :as time]
              [com.stuartsierra.component :as component]
              [us.edwardstx.common.spec :as specs]
              [clojure.spec.alpha :as s]))

(def headder {:alg :es256})
(def ec-cipher (crypto/create-cipher "ECIES" ))
(def exp-interval (atom (time/days 1)))

(defn get-auth-key [host]
  (d/chain (http/get (str host "/key"))
           #(:body %)
           bs/to-string
           crypto/decode-base64
           #(crypto/decode-public-key {:algorithm "ECDSA" :bytes %}))
  )

(defrecord Keys [key-pair auth-key env]
  component/Lifecycle

  (start [this]
    (let [auth-key-f (get-auth-key (:auth-host env))
          kp (crypto/decode-key-pair
           {:public-key {:algorithm "ECDSA"
                         :bytes (-> env :public-key crypto/decode-base64)}
            :private-key {:algorithm "ECDSA"
                          :bytes (-> env :private-key crypto/decode-base64)}})]
      (assoc this :key-pair kp :auth-key @auth-key-f)))

  (stop [this]
    (assoc this :key-pair nil)))

(s/def ::public-key ::specs/base64)
(s/def ::private-key ::specs/base64)
(s/def ::public-private-keys (s/keys :req-un [::public-key ::private-key]))
(s/def ::key-pair #(instance? java.security.KeyPair  %))
(s/def ::keys-record (s/keys :req-un [::key-pair]))


(defn new-keys [env]
  {:pre [(s/valid? ::public-private-keys env)]}
  (map->Keys {:env (select-keys env [:public-key :private-key :service-name :auth-host])}))

(defn sign [keys claims]
  {:pre [(s/valid? ::keys-record keys)]}
      (jwt/sign claims
                (-> keys :key-pair crypto/private-key)
                headder))

(defn unsign [keys token]
  (try
    (jwt/unsign token (-> keys :auth-key) headder)
    (catch Exception e
      nil)))

(defn decrypt [keys data]
  (crypto/decrypt
   (:key-pair keys)
   (crypto/decode-base64 data)
   ec-cipher))

(defn generate-key-pair []
  (let [k (crypto/generate-key-pair :key-size 256 :algorithm "ECDSA")
        kpm (crypto/get-key-pair-map k)]
    {:private-key (crypto/encode-base64-as-str (-> kpm :private-key :bytes))
     :public-key  (crypto/encode-base64-as-str (-> kpm :public-key  :bytes))}))

(defn create-self-signed-token [keys]
  (let [n (time/now)]
    (sign keys
          {:iss (-> keys :env :service-name)
           :sub (-> keys :env :service-name)
           :key (-> keys :env :public-key)
           :jti (str (java.util.UUID/randomUUID))
           :iat n
           :exp (time/plus n (time/minutes 5))})))

(comment

  (use 'us.edwardstx.common.keys :reload)

  (use 'config.core)

  (use 'clj-time.core)

  (defonce system (atom {}))

  (reset! system (com.stuartsierra.component/system-map
                 :keys (us.edwardstx.common.keys/new-keys config.core/env)))

  (swap! system com.stuartsierra.component/start)

  (defn gen-ksr [s]
    (us.edwardstx.common.keys/sign (:keys s)
     (let [n (clj-time.core/now)]
       {:iss "conf-management"
        :sub "conf-management"
        :jti (str (java.util.UUID/randomUUID))
        :key (-> s :keys :env :public-key)
        :iat n
        :exp (clj-time.core/plus n (clj-time.core/days 2))})))

 (gen-ksr @system)
 
  (defn extend-claims [keys claims]
    (let [n (time/now)]
      (assoc claims
             :iss (-> keys :env :service-name)
             :exp (time/plus n @exp-interval)
             :iat n)))

  (defn creat-claims
    ([sub]
     (creat-claims sub (str (java.util.UUID/randomUUID))))
    ([sub jti]
     {:sub sub :jti jti}))

  (def s (atom ()))







  )
