(ns clojure.realworld.shared-ui.views
  (:require [clojure.realworld.core-ui.interface :as core-ui]
            [re-frame.core :refer [dispatch subscribe]]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/views.cljs

(defn format-date
  [date]
  (.toDateString (js/Date. date)))

(defn tags-list
  [tags-list]
  [:ul.tag-list
   (for [tag tags-list]
     [:li.tag-default.tag-pill.tag-outline {:key tag} tag])])

(defn article-meta
  [{:keys [author createdAt favoritesCount favorited slug] 
    :or {slug "" author {:username ""}}}]
  (let [loading @(subscribe [core-ui/<loading])
        user @(subscribe [core-ui/<user])
        profile @(subscribe [core-ui/<profile])
        username (:username author)]
    [:div.article-meta
     [:a {:href (core-ui/url-for :profile :user-id username)}
      [:img {:src (:image author) :alt "user image"}] " "]
     [:div.info
      [:a.author {:href (core-ui/url-for :profile :user-id username)} username]
      [:span.date (format-date createdAt)]]
     (if (= (:username user) username)
       [:span
        [:a.btn.btn-sm.btn-outline-secondary {:href (core-ui/url-for :editor :slug slug)}
         [:i.ion-edit]
         [:span " Edit Article "]]
        " "
        [:a.btn.btn-outline-danger.btn-sm {:href     (core-ui/url-for :home)
                                           :on-click #(dispatch [core-ui/>delete-article slug])}
         [:i.ion-trash-a]
         [:span " Delete Article "]]]
       (when (seq user)
         [:span
          [:button.btn.btn-sm.action-btn.btn-outline-secondary {:on-click #(dispatch [core-ui/>toggle-follow-user username])
                                                                :class    (when (:toggle-follow-user loading) "disabled")}
           [:i {:class (if (:following profile) "ion-minus-round" "ion-plus-round")}]
           [:span (if (:following profile) (str " Unfollow " username) (str " Follow " username))]]
          " "
          [:button.btn.btn-sm.btn-primary {:on-click #(dispatch [core-ui/>toggle-favorite-article slug])
                                           :class    (cond
                                                       (not favorited) "btn-outline-primary"
                                                       (:toggle-favorite-article loading) "disabled")}
           [:i.ion-heart]
           [:span (if favorited " Unfavorite Post " " Favorite Post ")]
           [:span.counter "(" favoritesCount ")"]]]))]))

(defn articles-preview
  [{:keys [description slug createdAt title author favoritesCount favorited tagList] :or {slug "" author {:username ""}}}]
  (let [loading @(subscribe [core-ui/<loading])
        user @(subscribe [core-ui/<user])
        username (:username author)]
    [:div.article-preview
     [:div.article-meta
      [:a {:href (core-ui/url-for :profile :user-id username)}
       [:img {:src (:image author) :alt "user image"}]]
      [:div.info
       [:a.author {:href (core-ui/url-for :profile :user-id username)} username]
       [:span.date (format-date createdAt)]]
      (when (seq user)
        [:button.btn.btn-primary.btn-sm.pull-xs-right {:on-click #(dispatch [core-ui/>toggle-favorite-article slug])
                                                       :class    (cond
                                                                   (not favorited) "btn-outline-primary"
                                                                   (:toggle-favorite-article loading) "disabled")}
         [:i.ion-heart " "]
         [:span favoritesCount]])]
     [:a.preview-link {:href (core-ui/url-for :article :slug slug)}
      [:h1 title]
      [:p description]
      [:span "Read more ..."]
      [tags-list tagList]]]))                        ;; defined in Helpers section

(defn articles-list
  [articles loading-articles]
  [:div
   (if loading-articles
     [:div.article-preview
      [:p "Loading articles ..."]]
     (if (empty? articles)
       [:div.article-preview
        [:p "No articles are here... yet."]]
       (for [article articles]
         ^{:key (:slug article)} [articles-preview article])))])

(defn errors-list
  [errors]
  [:ul.error-messages
   (for [[k [v]] errors]
     ^{:key k} [:li (str k " " v)])])

;; -- Header -----------------------------------------------------------------
;;
(defn header
  []
  (let [user @(subscribe [core-ui/<user])
        active-page @(subscribe [core-ui/<active-page])]
    [:nav.navbar.navbar-light
     [:div.container
      [:a.navbar-brand {:href (core-ui/url-for :home)} "conduit"]
      (if (empty? user)
        [:ul.nav.navbar-nav.pull-xs-right
         [:li.nav-item
          [:a.nav-link {:href (core-ui/url-for :home) :class (when (= active-page :home) "active")} "Home"]]
         [:li.nav-item
          [:a.nav-link {:href (core-ui/url-for :login) :class (when (= active-page :login) "active")} "Sign in"]]
         [:li.nav-item
          [:a.nav-link {:href (core-ui/url-for :register) :class (when (= active-page :register) "active")} "Sign up"]]]
        [:ul.nav.navbar-nav.pull-xs-right
         [:li.nav-item
          [:a.nav-link {:href (core-ui/url-for :home) :class (when (= active-page :home) "active")} "Home"]]
         [:li.nav-item
          [:a.nav-link {:href (core-ui/url-for :editor :slug "new") :class (when (= active-page :editor) "active")}
           [:i.ion-compose "New Article"]]]
         [:li.nav-item
          [:a.nav-link {:href (core-ui/url-for :settings) :class (when (= active-page :settings) "active")}
           [:i.ion-gear-a "Settings"]]]
         [:li.nav-item
          [:a.nav-link {:class (when (= active-page :profile) "active")
                        :on-click #(dispatch [core-ui/>set-active-page {:page :profile :profile (:username user)}])}
           (:username user)
           [:img.user-pic {:src (:image user) :alt "user image"}]]]])]]))

;; -- Footer ------------------------------------------------------------------
;;
(defn footer
  []
  [:footer
   [:div.container
    [:a.logo-font {:href (core-ui/url-for :home)} "conduit"]
    [:span.attribution
     "An interactive learning project from "
     [:a {:href "https://thinkster.io"} "Thinkster"]
     ". Code & design licensed under MIT."]]]) 
