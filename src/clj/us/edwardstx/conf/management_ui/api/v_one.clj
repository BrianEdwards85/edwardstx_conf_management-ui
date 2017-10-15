(ns us.edwardstx.conf.management-ui.api.v-one
  (:require [us.edwardstx.conf.management-ui.orchestrator :as orchestrator]
            [us.edwardstx.common.security :refer [authorized]]
            [clojure.data.json :as json]
            [yada.yada :as yada]
            [manifold.deferred :as d]
            [us.edwardstx.conf.management-ui.data.conf :as conf]))

(defn get-keys [o ctx]
  (d/chain (orchestrator/get-keys o) json/write-str))

(defn get-key-values [o ctx]
  (let [k (-> ctx :parameters :path :key)]
    (d/chain (orchestrator/get-key-values o k) json/write-str)))

(defn get-services [o ctx]
  (d/chain (orchestrator/get-services o) json/write-str))

(defn get-service-key-values [o ctx]
  (let [service (-> ctx :parameters :path :service)]
    (d/chain (orchestrator/get-service-key-values o service) json/write-str)))

(defn set-service-key [o ctx]
  (let [service (-> ctx :parameters :path :service)
        b (-> ctx :body (assoc :service service))]
    (orchestrator/set-service-key o b)))

(defn remove-service-key-value [o ctx]
  (let [service (-> ctx :parameters :path :service)
        b (-> ctx :body )]
    (d/chain
     (orchestrator/remove-service-key-value o {:service service :key_path b})
     #(assoc (:response ctx) :body (json/write-str %) :status 200)
     )))

(defn build-v1-routes [o]
  (let [authorized (partial authorized (:keys o))]
    [["keys"
      (yada/resource {:methods
                      {:get {:produces "application/json"
                             :response (authorized (partial get-keys o))
                             }}})]
     ["services"
      (yada/resource {:methods
                      {:get {:produces "application/json"
                             :response (authorized (partial get-services o))
                             }}})]
     [["services/" :service]
      (yada/resource {:produces "application/json"
                      :parameters {:path {:service String}}
                      :methods
                      {:get {:response (authorized (partial get-service-key-values o))}
                       :delete {:consumes "text/plain"
                                :response (authorized (partial remove-service-key-value o))}
                       :post {:consumes "application/json"
                              :response (authorized (partial set-service-key o))}}})]
     [["keys/" :key]
      (yada/resource {:methods
                      {:get {:produces "application/json"
                             :parameters {:path {:key String}}
                             :response (authorized (partial get-key-values o))
                             }}})]
     ["echo"
      (yada/resource {:methods
                      {:get {:produces "text/plain"
                             :response (fn [ctx]
                                         (clojure.pprint/pprint ctx)
                                         (str "UID: " (json/write-str (:jwt ctx))))}
                       }})]])
  )
