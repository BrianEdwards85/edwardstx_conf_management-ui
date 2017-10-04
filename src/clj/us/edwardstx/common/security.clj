(ns us.edwardstx.common.security
  (:require [us.edwardstx.common.keys :as keys]
            [clojure.data.json :as json]
            [yada.yada :as yada]
            ))

(declare ^:dynamic *jwt*)

(defn no-authorization [ctx]
  (assoc (:response ctx)
         :status 401
         :body "No Athorization"))

(defn authorized
  ([keys f] (authorized keys f no-authorization))
  ([keys f e] (fn [ctx]
           (if-let [j (get-in ctx [:cookies "uid"])]
             (if-let [c (keys/unsign keys j)]
               (binding [*jwt* c]
                 (f (assoc ctx :jwt c)))
                   (e ctx))
                 (e ctx)))))


(defn redirect [ctx]
  (assoc (:response ctx)
         :status 302
         :headers {"location" (str
                               "https://eight.edwardstx.us/auth/"
                               "?r="
                               "https://home.edwardstx.us/management-ui/")}))

(defn build-auth-routes [keys]
  [["user"
    (yada/resource {:methods
                    {:get {:produces "application/json"
                           :response (authorized keys #(-> % :jwt json/write-str))
                           }}
                    })
    ]])
