(ns us.edwardstx.conf.management-ui.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [re-frame.core :as re-frame]
              [bidi.bidi :as bidi]
              [accountant.core :as accountant]))

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

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; -------------------------
;; Initialize app

(def accountant-configuration
  {:nav-handler
   (fn [path] (secretary/dispatch! path))
   :path-exists?
   (fn [path] (secretary/locate-route path))})


(defn ^:export init! []
  (accountant/configure-navigation! accountant-configuration)
  (re-frame/dispatch-sync [:initialize])
  (accountant/dispatch-current!)
  (reagent/render [main-panel] (.getElementById js/document "app")))

(comment 
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
