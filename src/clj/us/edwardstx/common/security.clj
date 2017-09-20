(ns us.edwardstx.common.security
  (:require [us.edwardstx.common.keys :as keys]))

(declare ^:dynamic *jwt*)

(defn no-authorization [ctx]
  (assoc (:response ctx)
         :status 401
         :body "No Athorization"))

(defn authorized
  ([o f] (authorized o f no-authorization))
  ([o e f] (fn [ctx]
           (if-let [j (get-in ctx [:cookies "uid"])]
             (if-let [c (keys/unsign (:keys o) j)]
               (binding [*jwt* c]
                 (f (assoc ctx :jwt c)))
                   (e ctx))
                 (e ctx)))))
