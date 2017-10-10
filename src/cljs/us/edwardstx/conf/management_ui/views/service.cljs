(ns us.edwardstx.conf.management-ui.views.service
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defn service []
  (let [service (re-frame/subscribe [:service])
        service-keys (re-frame/subscribe [:services-keys])]
    (reagent/create-class
     {
      :component-will-mount #(re-frame/dispatch [:get-service-keys @service])

      :display-name "service-keys"

      :render
      (fn []
        [:div
         [:h2 (str @service " service keys")]
         (if @service-keys
           [:table
            [:tbody
             (for [service-key @service-keys]
               ^{:key (str "SERVICE_KEY_" (:key_path service-key))}
               [:tr
                [:td (:key_path service-key)]
                [:td (:conf_value service-key)]]

               )]
            ]



           )])
      }




     )



    ))

