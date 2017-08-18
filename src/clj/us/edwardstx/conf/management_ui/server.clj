(ns us.edwardstx.conf.management-ui.server
  (:require [us.edwardstx.conf.management-ui.handler :refer [app]]
            [config.core :refer [env]]
            )
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (println "Run")
   ))
