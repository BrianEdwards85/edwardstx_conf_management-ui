(ns us.edwardstx.conf.management-ui.views.service
  (:require [re-frame.core :as re-frame]
            [us.edwardstx.conf.management-ui.views.common :as c]
            [reagent.core :as reagent]))

(defn update-service-key-value [service-key service-key-value]
    (re-frame/dispatch [:set-service-key
                        (:service service-key)
                        (assoc service-key-value :key (:key_path service-key))
                        ]))

(defn add-value-modal [path]
  (let [new-value (reagent/atom "")]
    (fn [path]
      [c/modal
       (fn [] (reset! path nil))
       [:div
        [:input {:placeholder "New Value" :value @new-value :on-change #(reset! new-value (-> % .-target .-value))}]
        [:button.btn.btn-outline-light.btn-sm {:on-click #(do (update-service-key-value @path {:value @new-value})
                                                              (reset! path nil)
                                                              )}[c/glyphicon "plus"]]]])))

(defn conf-key-value [service-key service-key-add]
  (let [key-values (re-frame/subscribe [:key-values])]
    [:div.form-inline
     [:select.form-control  {:value (:id service-key)
                             :on-change #(let [value (-> % .-target .-value)]
                                           (if (= value "NEW_VALUE")
                                             (do
                                               (re-frame/dispatch [:end-service-key-edit])
                                               (reset! service-key-add service-key))
                                             (update-service-key-value service-key {:id value})))}
      [:option {:value "NEW_VALUE"} "NEW VALUE"]
      (for [key-value @key-values]
        ^{:key (str "SERVICE-KEY-" (:key_path service-key) "-VALUE-" (:id key-value))}
        [:option  {:value (:id key-value)} (:conf_value key-value)])]
     [:button.btn.btn-outline-light.btn-sm {:on-click #(re-frame/dispatch [:end-service-key-edit])} [c/glyphicon "close"]]]
    ))

(defn service-key-row [service-key service-key-add]
  (let [service-key-edit (re-frame/subscribe [:service-key-edit])]
    (fn [service-key service-key-add]
      [:tr
       [:td (:key_path service-key)]
       [:td
        {:on-double-click #(re-frame/dispatch [:get-key-values (:key_path service-key)])}
        (if (= @service-key-edit (:key_path service-key))
          [conf-key-value service-key service-key-add]
          (:conf_value service-key))]
       [:td [:button.btn.btn-outline-light.btn-sm
             {:on-click #(re-frame/dispatch [:remove-service-key
                                             (:service service-key)
                                             (:key_path service-key)])}
             [c/glyphicon "remove"]]]
       ])))

(defn filter-keys [keys service-keys]
  (let [key-set (apply hash-set (map :key_path service-keys))]
    (sort (filter #(not (contains? key-set %)) (map :key_path keys)))))

(defn service []
  (let [service (re-frame/subscribe [:service])
        service-keys (re-frame/subscribe [:services-keys])
        service-key-edit (re-frame/subscribe [:service-key-edit])
        keys (re-frame/subscribe [:keys])

        key-add (reagent/atom false)
        service-key-add (reagent/atom nil)

        ]
    (reagent/create-class
     {
      :component-will-mount #(re-frame/dispatch [:get-service-keys @service])

      :display-name "service-keys"

      :render
      (fn []
        [:div
         (if @service-key-add
           [add-value-modal service-key-add])
         [:h2 (str @service " service keys")]
         [:button.btn.btn-outline-light.btn-sm {:on-click #(do
                                                             (re-frame/dispatch [:get-keys])
                                                             (reset! key-add true))} [c/glyphicon "plus"]]
         (if @service-keys
           [:table#service-keys.table
            (into
             [:tbody
              (if (and @key-add @keys)

                [:tr
                 [:td
                  [:div.form-inline
                   [:select.form-control {:on-change #(re-frame/dispatch [:get-key-values (-> % .-target .-value)])}
         ;;          [:option {:value "NEW_VALUE"} "NEW VALUE"]
                    [:option ""]
                    (for [key (filter-keys @keys @service-keys)]
                      ^{:key (str "NEW-KEY-" key)}
                      [:option key])]
                   [:button.btn.btn-outline-light.btn-sm {:on-click #(reset! key-add false)}
                    [c/glyphicon "close"]]]
                  ]
                 [:td
                  (if @service-key-edit
                    [conf-key-value {:service @service :key_path @service-key-edit} service-key-add]




                    )]])]
             (for [service-key (sort-by :key_path @service-keys)]
               ^{:key (str "SERVICE-KEY-" (:key_path service-key))}
               [service-key-row (assoc service-key :service @service) service-key-add]

               ))])])})))

