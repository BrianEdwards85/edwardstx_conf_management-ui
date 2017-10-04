(ns us.edwardstx.conf.management-ui.routes)

(def app-routes ["/management-ui"  [["/" ::root]
                                    [["/service/" :service] ::service]]])
