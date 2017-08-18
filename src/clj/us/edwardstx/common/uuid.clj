(ns us.edwardstx.common.uuid)

(defn uuid [] (str (java.util.UUID/randomUUID)))
