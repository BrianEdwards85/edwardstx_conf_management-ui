(ns us.edwardstx.conf.management-ui.views.service
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defn service-key-row [service-key service-key-edit key-values]
  [:tr
   [:td (:key_path service-key)]
   [:td
    {:on-double-click #(re-frame/dispatch [:get-key-values (:key_path service-key)])}
    (if (= @service-key-edit (:key_path service-key))
      [:div.form-inline
       [:select.form-control  {:value (:id service-key)}
        [:option "NEW VALUE"]
        (for [key-value @key-values]
          ^{:key (str "SERVICE-KEY-" (:key_path service-key) "-VALUE-" (:id key-value))}
          [:option  {:value (:id key-value)}
;;           (let [val {:value (:id key-value)}]
;;             (if (= (:id key-value) (:id service-key))
;;               (assoc val :selected true)
;;               val))
           (:conf_value key-value)]

          )]

       [:button.edit-close.btn.btn-sm {:on-click #(reset! edit false)}"X"]
       ]
      (:conf_value service-key)

      )
    ]])

(defn service []
  (let [service (re-frame/subscribe [:service])
        service-keys (re-frame/subscribe [:services-keys])
        service-key-edit (re-frame/subscribe [:service-key-edit])
        key-values (re-frame/subscribe [:key-values])]
    (reagent/create-class
     {
      :component-will-mount #(re-frame/dispatch [:get-service-keys @service])

      :display-name "service-keys"

      :render
      (fn []
        [:div
         [:h2 (str @service " service keys")]
         (if @service-keys
           [:table#service-keys.table
            (into 
             [:tbody]
             (for [service-key @service-keys]
               ^{:key (str "SERVICE-KEY-" (:key_path service-key))}
               [service-key-row (assoc service-key :service @service) service-key-edit key-values]

               ))])])})))

