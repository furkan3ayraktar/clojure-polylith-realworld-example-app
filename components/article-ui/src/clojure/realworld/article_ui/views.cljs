(ns clojure.realworld.article-ui.views
  (:require [clojure.realworld.core-ui.interface :as core-ui]
            [clojure.realworld.shared-ui.interface :as shared-ui]
            [clojure.realworld.shared.interface :as shared]
            [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [clojure.string :as str]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/views.cljs

;; -- Profile -----------------------------------------------------------------
;;
(defn profile
  []
  (let [{:keys [image username bio following] :or {username ""}} @(subscribe [core-ui/<profile])
        {:keys [author favorites]} @(subscribe [core-ui/<filter])
        loading @(subscribe [core-ui/<loading])
        articles @(subscribe [core-ui/<articles])
        user @(subscribe [core-ui/<user])]
    [:div.profile-page
     [:div.user-info
      [:div.container
       [:div.row
        [:div.col-xs-12.col-md-10.offset-md-1
         [:img.user-img {:src image :alt "user image"}]
         [:h4 username]
         [:p bio]
         (if (= (:username user) username)
           [:a.btn.btn-sm.btn-outline-secondary.action-btn {:href (core-ui/url-for :settings)}
            [:i.ion-gear-a] " Edit Profile Settings"]
           [:button.btn.btn-sm.action-btn.btn-outline-secondary {:on-click #(dispatch [core-ui/>toggle-follow-user username])
                                                                 :class    (when (:toggle-follow-user loading) "disabled")}
            [:i {:class (if following "ion-minus-round" "ion-plus-round")}]
            [:span (if following (str " Unfollow " username) (str " Follow " username))]])]]]]
     [:div.container
      [:div.row
       [:div.col-xs-12.col-md-10.offset-md-1
        [:div.articles-toggle
         [:ul.nav.nav-pills.outline-active
          [:li.nav-item
           [:a.nav-link {:class (when author " active")
                         :on-click #(dispatch [core-ui/>get-articles {:author username}])} "My Articles"]]
          [:li.nav-item
           [:a.nav-link {:class (when favorites "active")
                         :on-click #(dispatch [core-ui/>get-articles {:favorited (:username user)}])} "Favorited Articles"]]]]
        [shared-ui/articles-list articles (:articles loading)]]]]]))

;; -- Editor ------------------------------------------------------------------
;;
(defn editor
  []
  (let [{:keys [title description body tagList slug]
         :as active-article} @(subscribe [core-ui/<active-article])
        tagList-string (if (and tagList (seq tagList))
                          (str/join " " tagList)
                          "")
        default {:title (or title "") :description (or description "") :body (or body "") :tagList tagList-string}
        content (r/atom default)
        upsert-article (fn [event content slug]
                         (.preventDefault event)
                         (let [tagList-array (shared/clean-tags (:tagList content))
                               clean-content (assoc content :tagList tagList-array)]
                           (if slug
                             ;; Update existing article
                             (dispatch [core-ui/>upsert-article {:slug slug :article clean-content}])
                             ;; Create new article
                             (dispatch [core-ui/>upsert-article {:article clean-content}]))))]
    [:div.editor-page
     [:div.container.page
      [:div.row
       [:div.col-md-10.offset-md-1.col-xs-12
        [:form
         [:fieldset
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Article Title"
                                                 :default-value (:title content)
                                                 :on-change     #(swap! content assoc :title (-> % .-target .-value))}]]
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "What's this article about?"
                                 :default-value (:description content)
                                 :on-change     #(swap! content assoc :description (-> % .-target .-value))}]]
          [:fieldset.form-group
           [:textarea.form-control {:rows          "8"
                                    :placeholder   "Write your article (in markdown)"
                                    :default-value (:body content)
                                    :on-change     #(swap! content assoc :body (-> % .-target .-value))}]]
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "Enter tags"
                                 :default-value tagList
                                 :on-change     #(swap! content assoc :tagList (-> % .-target .-value))}]
           [:div.tag-list]]
          [:button.btn.btn-lg.btn-primary.pull-xs-right {:on-click #(upsert-article % @content slug)}
           (if slug
             "Update Article"
             "Publish Article")]]]]]]]))

;; -- Article -----------------------------------------------------------------
;;
(defn article
  []
  (let [default {:body ""}
        comment (r/atom default)
        post-comment (fn [event default]
                       (.preventDefault event)
                       (dispatch [core-ui/>post-comment {:body (get @comment :body)}])
                       (reset! comment default))]
    (fn []
      (let [active-article @(subscribe [core-ui/<active-article])
            user @(subscribe [core-ui/<user])
            comments @(subscribe [core-ui/<comments])
            errors @(subscribe [core-ui/<errors])
            loading @(subscribe [core-ui/<loading])]
        [:div.article-page
         [:div.banner
          [:div.container
           [:h1 (:title active-article)]
           [shared-ui/article-meta active-article]]]
         [:div.container.page
          [:div.row.article-content
           [:div.col-md-12
            [:p (:body active-article)]]]
          [shared-ui/tags-list (:tagList active-article)]
          [:hr]
          [:div.article-actions
           [shared-ui/article-meta active-article]]
          [:div.row
           [:div.col-xs-12.col-md-8.offset-md-2
            (when (:comments errors)
              [shared-ui/errors-list (:comments errors)])
            (if-not (empty? user)
              [:form.card.comment-form
               [:div.card-block
                [:textarea.form-control {:placeholder "Write a comment..."
                                         :rows        "3"
                                         :value       (:body @comment)
                                         :on-change   #(swap! comment assoc :body (-> % .-target .-value))}]]
               [:div.card-footer
                [:img.comment-author-img {:src (:image user) :alt "user image"}]
                [:button.btn.btn-sm.btn-primary {:class    (when (:comments loading) "disabled")
                                                 :on-click #(post-comment % default)} "Post Comment"]]]
              [:p
               [:a {:href (core-ui/url-for :register)} "Sign up"]
               " or "
               [:a {:href (core-ui/url-for :login)} "Sign in"]
               " to add comments on this article."])
            (if (:comments loading)
              [:div
               [:p "Loading comments ..."]]
              (if (empty? comments)
                [:div]
                (for [{:keys [id createdAt body author]} comments]
                  ^{:key id} [:div.card
                              [:div.card-block
                               [:p.card-text body]]
                              [:div.card-footer
                               [:a.comment-author {:href (core-ui/url-for :profile :user-id (:username author))}
                                [:img.comment-author-img {:src (:image author) :alt "user image"}]]
                               " "
                               [:a.comment-author {:href (core-ui/url-for :profile :user-id (:username author))} (:username author)]
                               [:span.date-posted (shared-ui/format-date createdAt)]
                               (when (= (:username user) (:username author))
                                 [:span.mod-options {:on-click #(dispatch [core-ui/>delete-comment id])}
                                  [:i.ion-trash-a]])]])))]]]]))))
