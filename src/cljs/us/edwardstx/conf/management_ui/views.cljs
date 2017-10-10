(ns us.edwardstx.conf.management-ui.views
  (:require [re-frame.core :as re-frame]
            [us.edwardstx.conf.management-ui.routes :as routes]
            [us.edwardstx.conf.management-ui.views.service :refer [service]]
            [us.edwardstx.conf.management-ui.views.services :refer [services]]))

(defn tophat [user]
  [:div.tophat
   [:a.title {:href "/management-ui/" } "Configuration Management UI"]
   [:div.user (:sub @user)] ])

(defn pages [page]
  [:div.page
   (case @page
     ::routes/root [services]
     ::routes/service [service]

     [:div (str "Page: " @page)]
     )])

(defn main-panel []
  (let [page (re-frame/subscribe [:page])
        user (re-frame/subscribe [:user])]
    (fn []
      (if (nil? @user)
        [:div {:class "loader"} "Loading"]
        [:div
         [tophat user]
         [pages page]
        ])



      )))

