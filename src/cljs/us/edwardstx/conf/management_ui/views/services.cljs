(ns us.edwardstx.conf.management-ui.views.services
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))


(defn services []
  (let [services (re-frame/subscribe [:services])]
    (reagent/create-class
     {
      :component-will-mount #(re-frame/dispatch [:get-services])

      :display-name "services"

      :render
      (fn []
        [:div
         [:h2  "Services"]
         (if @services
           [:table
            [:tbody
             (for [service @services]
               ^{:key (str "SERVICE_ROW_" service)}
               [:tr
                [:td 
                 [:a {:href (str "/management-ui/service/" service)} service]]

                ]
               )]])])
      }))
  )
