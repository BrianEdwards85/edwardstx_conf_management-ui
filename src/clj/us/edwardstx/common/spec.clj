(ns us.edwardstx.common.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::non-empty-string (s/and string? not-empty))
(s/def ::positive-int (s/and integer? #(> % 0)))

(def base64-regex #"^[A-Za-z0-9+/]+[=]{0,2}$")
(s/def ::base64 (s/and ::non-empty-string #(re-matches base64-regex %)))

(def hex-regex #"^[A-Za-z0-9]+$")
(s/def ::hex (s/and ::non-empty-string #(re-matches hex-regex %)))

(def uuid-regex #"^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
(s/def ::uuid (s/and ::non-empty-string #(re-matches uuid-regex %)))
