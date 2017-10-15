(ns us.edwardstx.conf.management-ui.views.common)

(defn glyphicon [n]
 ;; [:span {:class (str "glyphicon glyphicon-" n) }]
  [:img {:src (str "/management-ui/assets/png/" n ".png") :alt n :width 26 :height 26}]
  )

(defn modal [close content]
  [:div.add-modal
   [:div.content
    [:div.headder
     [:button {:class "btn btn-outline-light btn-sm"
               :type "button"
               :on-click close}
      [glyphicon "close"]]]
    content]])
