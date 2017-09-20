(ns us.edwardstx.conf.management-ui.html
  (:require [hiccup.page :refer [include-js include-css html5]]
            [us.edwardstx.common.security :refer [authorized]]
            [clojure.java.io :as io]
            [yada.yada :as yada]))

(def mount-target [:div#app [:div.loader ]])

(defn head [t]
  [:head
   (if t [:title t])
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:link
    {:type "text/css"
     :rel "stylesheet"
     :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css"
;;     :integrity "sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
     :crossorigin "anonymous"}]
   (include-css "/management-ui/assets/css/main.css"
  ;;  (if (env :dev) "/css/site.css" "/css/site.min.css")
    )])

(defn loading-page [ctx]
  (html5
    (head "loading")
    [:body {:class "body-container"}
     mount-target
     [:script
      {:src "https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"
  ;;     :integrity "sha256-Sk3sfKjyVntDJ8grhzyNfdd090uQCdL/ZUMagVRpPeo="
       :crossorigin "anonymous"
       :type "text/javascript"}]
     [:script
      {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
       :integrity "sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
       :crossorigin "anonymous"
       :type "text/javascript"}]
     (include-js "/management-ui/assets/js/app.js")]))

(defn redirect [ctx]
  (assoc (:response ctx)
         :status 302
         :headers {"location" (str
                               "https://eight.edwardstx.us/auth/"
                               "?r="
                               "https://home.edwardstx.us/management-ui/")}))

(defn build-routes [o]
  ["" [["assets/js/" (yada/as-resource (io/file "target/cljsbuild/public/js"))
        ]
       ["assets/css/" (yada/as-resource (io/file "resources/public/css/"))]
       ["" (yada/resource  {:methods {
                                     :get {
                                           :produces #{"text/html"}
                                           :response (authorized o redirect loading-page)
                                           }
                                     }})]]])
