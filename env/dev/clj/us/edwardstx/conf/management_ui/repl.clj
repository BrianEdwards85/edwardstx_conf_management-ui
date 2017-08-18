(ns us.edwardstx.conf.management-ui.repl
  (:use us.edwardstx.conf.management-ui.handler
        figwheel-sidecar.repl-api
        [ring.middleware file-info file]))

(defonce server (atom nil))

