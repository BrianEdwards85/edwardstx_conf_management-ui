(ns us.edwardstx.conf.management-ui.html
  (:require [hiccup.page :refer [include-js include-css html5]]
            [us.edwardstx.common.security :refer [authorized]]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]
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
     :href "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css"
     :integrity "sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M"
     :crossorigin "anonymous"}]
   (include-css "/management-ui/assets/css/main.css")])

(defn loading-page [ctx]
  (html5
    (head "loading")
    [:body {:class "body-container"}
     mount-target
     [:script
      {:src "https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"
  ;;     :integrity "sha256-Sk3sfKjyVntDJ8grhzyNfdd090uQCdL/ZUMagVRpPeo="
       :crossorigin "anonymous"
       :type "text/javascript"}]
     [:script
      {:src "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"
       :integrity "sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4"
       :crossorigin "anonymous"
       :type "text/javascript"}]
     [:script
      {:src "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js"
       :integrity "sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1"
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
  ["" [
       ["assets/" (new-classpath-resource "public") ]
       [true (yada/resource  {:methods {
                                     :get {
                                           :produces #{"text/html"}
                                           :response (authorized (:keys o) loading-page redirect)
                                           }
                                     }})]]])
