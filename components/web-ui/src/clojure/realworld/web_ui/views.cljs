(ns clojure.realworld.web-ui.views
  (:require [clojure.realworld.core-ui.interface :as core-ui]
            [clojure.realworld.shared-ui.interface :as shared-ui]
            [clojure.realworld.auth-ui.interface :as auth-ui]
            [clojure.realworld.article-ui.interface :as article-ui]
            [clojure.realworld.home-ui.interface :as home-ui]
            [re-frame.core :refer [subscribe]]))

;; -- Main App Component -----------------------------------------------------
;;
(defn pages
  [page-name]
  (let [page-keyword (if (map? page-name)
                       (keyword (:name page-name))
                        page-name)]

    (case page-keyword
      :home [home-ui/home]
      :login [auth-ui/login]
      :register [auth-ui/register]
      :profile [article-ui/profile]
      :settings [auth-ui/settings]
      :editor [article-ui/editor]
      :article [article-ui/article]
      [home-ui/home])))

(defn app
  []
  (let [active-page @(subscribe [core-ui/<active-page])]
    [:div
     [shared-ui/header]
     [pages active-page]
     [shared-ui/footer]]))
