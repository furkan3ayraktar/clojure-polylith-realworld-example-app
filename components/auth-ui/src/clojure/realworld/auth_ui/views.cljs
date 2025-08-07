(ns clojure.realworld.auth-ui.views
  (:require [clojure.realworld.core-ui.interface :as core-ui]
            [clojure.realworld.shared-ui.interface :as shared-ui]
            [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/views.cljs

;; -- Login -------------------------------------------------------------------
;;
(defn login
  []
  (let [default {:email "" :password ""}
        credentials (r/atom default)]
    (fn []
      (let [{:keys [email password]} @credentials
            loading @(subscribe [core-ui/<loading])
            errors @(subscribe [core-ui/<errors])
            login-user (fn [event credentials]
                         (.preventDefault event)
                         (dispatch [core-ui/>login credentials]))]
        [:div.auth-page
         [:div.container.page
          [:div.row
           [:div.col-md-6.offset-md-3.col-xs-12
            [:h1.text-xs-center "Sign in"]
            [:p.text-xs-center
             [:a {:href (core-ui/url-for :register)} "Need an account?"]]
            (when (:login errors)
              [shared-ui/errors-list (:login errors)])
            [:form {:on-submit #(login-user % @credentials)}
             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "text"
                                                    :placeholder "Email"
                                                    :value       email
                                                    :on-change   #(swap! credentials assoc :email (-> % .-target .-value))
                                                    :disabled    (:login loading)}]]
             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "password"
                                                    :placeholder "Password"
                                                    :value       password
                                                    :on-change   #(swap! credentials assoc :password (-> % .-target .-value))
                                                    :disabled    (:login loading)}]]
             [:button.btn.btn-lg.btn-primary.pull-xs-right {:class (when (:login loading) "disabled")} "Sign in"]]]]]]))))

;; -- Register -----------------------------------------------------------------
;;
(defn register
  []
  (let [default {:username "" :email "" :password ""}
        registration (r/atom default)]
    (fn []
      (let [{:keys [username email password]} @registration
            loading @(subscribe [core-ui/<loading])
            errors @(subscribe [core-ui/<errors])
            register-user (fn [event registration]
                           (.preventDefault event)
                           (dispatch [core-ui/>register-user registration]))]
        [:div.auth-page
         [:div.container.page
          [:div.row
           [:div.col-md-6.offset-md-3.col-xs-12
            [:h1.text-xs-center "Sign up"]
            [:p.text-xs-center
             [:a {:href (core-ui/url-for :login)} "Have an account?"]]
            (when (:register errors)
              [shared-ui/errors-list (:register errors)])
            [:form {:on-submit #(register-user % @registration)}
             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "text"
                                                    :placeholder "Your Name"
                                                    :value       username
                                                    :on-change   #(swap! registration assoc :username (-> % .-target .-value))
                                                    :disabled    (:register-user loading)}]]
             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "text"
                                                    :placeholder "Email"
                                                    :value       email
                                                    :on-change   #(swap! registration assoc :email (-> % .-target .-value))
                                                    :disabled    (:register-user loading)}]]
             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "password"
                                                    :placeholder "Password"
                                                    :value       password
                                                    :on-change   #(swap! registration assoc :password (-> % .-target .-value))
                                                    :disabled    (:register-user loading)}]]
             [:button.btn.btn-lg.btn-primary.pull-xs-right {:class (when (:register-user loading) "disabled")} "Sign up"]]]]]]))))

;; -- Settings ----------------------------------------------------------------
;;
(defn settings
  []
  (let [{:keys [bio email image username] :as user} @(subscribe [core-ui/<user])
        default {:bio bio :email email :image image :username username}
        loading @(subscribe [core-ui/<loading])
        user-update (r/atom default)
        logout-user (fn [event]
                      (.preventDefault event)
                      (dispatch [core-ui/>logout]))
        update-user (fn [event updated-user]
                      (.preventDefault event)
                      (dispatch [core-ui/>update-user updated-user]))]
    [:div.settings-page
     [:div.container.page
      [:div.row
       [:div.col-md-6.offset-md-3.col-xs-12
        [:h1.text-xs-center "Your Settings"]
        [:form
         [:fieldset
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "URL of profile picture"
                                 :default-value (:image user)
                                 :on-change     #(swap! user-update assoc :image (-> % .-target .-value))}]]
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Your Name"
                                                 :default-value (:username user)
                                                 :on-change     #(swap! user-update assoc :username (-> % .-target .-value))
                                                 :disabled      (:update-user loading)}]]
          [:fieldset.form-group
           [:textarea.form-control.form-control-lg {:rows          "8"
                                                    :placeholder   "Short bio about you"
                                                    :default-value (:bio user)
                                                    :on-change     #(swap! user-update assoc :bio (-> % .-target .-value))
                                                    :disabled      (:update-user loading)}]]
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Email"
                                                 :default-value (:email user)
                                                 :on-change     #(swap! user-update assoc :email (-> % .-target .-value))
                                                 :disabled      (:update-user loading)}]]
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "password"
                                                 :placeholder   "Password"
                                                 :default-value ""
                                                 :on-change     #(swap! user-update assoc :password (-> % .-target .-value))
                                                 :disabled      (:update-user loading)}]]
          [:button.btn.btn-lg.btn-primary.pull-xs-right {:on-click #(update-user % @user-update)
                                                         :class    (when (:update-user loading) "disabled")} "Update Settings"]]]
        [:hr]
        [:button.btn.btn-outline-danger {:on-click #(logout-user %)} "Or click here to logout."]]]]])) 
