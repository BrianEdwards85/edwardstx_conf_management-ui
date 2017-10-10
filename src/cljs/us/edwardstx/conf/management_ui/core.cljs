(ns us.edwardstx.conf.management-ui.core
    (:require [reagent.core :as reagent :refer [atom]]
              [us.edwardstx.conf.management-ui.handlers :refer [reg-handlers]]
              [us.edwardstx.conf.management-ui.subs :refer [reg-subs]]
              [us.edwardstx.conf.management-ui.views :refer [main-panel]]
              [us.edwardstx.conf.management-ui.routes :as routes]
              [re-frame.core :as re-frame]
              [bidi.bidi :as bidi]
              [accountant.core :as accountant]))

(defn accountant-configuration [routes]
  {:nav-handler
   (fn [path]
     (println path)
     (re-frame/dispatch [:nav (bidi/match-route routes path)]))
   :path-exists?
   (fn [path]
     (boolean (bidi/match-route routes path)))})

(defn ^:export init! []
  (reg-handlers)
  (reg-subs)
  (accountant/configure-navigation! (accountant-configuration routes/app-routes))
  (re-frame/dispatch-sync [:initialize])
  (accountant/dispatch-current!)
  (reagent/render [main-panel] (.getElementById js/document "app")))

(comment


  ;; -------------------------
  ;; Views

  (defn home-page []
    [:div [:h2 "Welcome to management-ui"]
     [:div [:a {:href "/about"} "go to about page"]]])

  (defn about-page []
    [:div [:h2 "About management-ui"]
     [:div [:a {:href "/"} "go to the home page"]]])

  ;; -------------------------
  ;; Routes

  (def page (atom #'home-page))

  (defn current-page []
    [:div [@page]])


  ;; -------------------------
  ;; Initialize app


  (def accountant-configuration
    {:nav-handler
     (fn [path] (secretary/dispatch! path))
     :path-exists?
     (fn [path] (secretary/locate-route path))})

  (secretary/defroute "/" []
    (reset! page #'home-page))

  (secretary/defroute "/about" []
    (reset! page #'about-page))
  (defn mount-root []
    (reagent/render [current-page] (.getElementById js/document "app")))

  (defn init! []
    (accountant/configure-navigation!
     {:nav-handler
      (fn [path]
        (secretary/dispatch! path))
      :path-exists?
      (fn [path]
        (secretary/locate-route path))})
    (accountant/dispatch-current!)
    (mount-root)))
