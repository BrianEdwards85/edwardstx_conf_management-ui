(ns us.edwardstx.conf.management-ui.views
  (:require [re-frame.core :as re-frame]))

(defn main-panel []
  (let [page (re-frame/subscribe [:page])
        user (re-frame/subscribe [:user])]
    (fn []
      (if (nil? @user)
        [:div {:class "loader"} "Loading"]
        [:div
         [:div (str "Page: " @page)]
         [:div (str "User: " (:sub @user))]]


        )



      )))

