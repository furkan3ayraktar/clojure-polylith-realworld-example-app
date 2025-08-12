(ns clojure.realworld.home-ui.views
  (:require [clojure.realworld.core-ui.interface :as core-ui]
            [clojure.realworld.shared-ui.interface :as shared-ui]
            [re-frame.core :refer [dispatch subscribe]]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/views.cljs

;; -- Home --------------------------------------------------------------------
;;
(defn home
  []
  (let [filter @(subscribe [core-ui/<filter])
        active-filter @(subscribe [core-ui/<active-filter])
        tags @(subscribe [core-ui/<tags])
        loading @(subscribe [core-ui/<loading])
        articles @(subscribe [core-ui/<articles])
        articles-count @(subscribe [core-ui/<articles-count])
        user @(subscribe [core-ui/<user])
        get-articles (fn [event params]
                       (.preventDefault event)
                       (dispatch [core-ui/>get-articles params]))
        get-feed-articles (fn [event params]
                            (.preventDefault event)
                            (dispatch [core-ui/>get-feed-articles params]))]
    [:div.home-page
     (when (empty? user)
       [:div.banner
        [:div.container
         [:h1.logo-font "conduit"]
         [:p "A place to share your knowledge."]]])
     [:div.container.page
      [:div.row
       [:div.col-md-9
        [:div.feed-toggle
         [:ul.nav.nav-pills.outline-active
          (when (seq user)
            [:li.nav-item
             [:a.nav-link {:href     (core-ui/url-for :home)
                           :class    (when (= active-filter :feed) "active")
                           :on-click #(get-feed-articles % {:offset 0
                                                            :limit  10})} "Your Feed"]])
          [:li.nav-item
           [:a.nav-link {:href     (core-ui/url-for :home)
                         :class    (when (= active-filter :all) "active")
                         :on-click #(get-articles % {:offset 0
                                                     :limit  10})} "Global Feed"]] ;; first argument: % is browser event, second: map of filter params
          (when (:tag filter)
            [:li.nav-item
             [:a.nav-link.active
              [:i.ion-pound] (str " " (:tag filter))]])]]
        [shared-ui/articles-list articles (:articles loading)]
        (when-not (or (:articles loading) (< articles-count 10))
          [:ul.pagination
           (for [offset (range (/ articles-count 10))]
             ^{:key offset} [:li.page-item {:class    (when (= (* offset 10) (:offset filter)) "active")
                                            :on-click #(get-articles % (if (:tag filter)
                                                                         {:offset (* offset 10)
                                                                          :tag    (:tag filter)
                                                                          :limit  10}
                                                                         {:offset (* offset 10)
                                                                          :limit  10}))}
                             [:a.page-link {:href (core-ui/url-for :home)} (inc offset)]])])]
       [:div.col-md-3
        [:div.sidebar
         [:p "Popular Tags"]
         (if (:tags loading)
           [:p "Loading tags ..."]
           [:div.tag-list
            (for [tag tags]
              ^{:key tag} [:a.tag-pill.tag-default {:href     (core-ui/url-for :home)
                                                    :on-click #(get-articles % {:tag    tag
                                                                                :limit  10
                                                                                :offset 0})}
                           tag])])]]]]])) 
