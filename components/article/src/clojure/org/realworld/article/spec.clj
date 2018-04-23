(ns clojure.org.realworld.article.spec
  (:require [clojure.org.realworld.spec.interface :as spec]
            [clojure.org.realworld.profile.spec :as profile-spec]
            [spec-tools.data-spec :as ds]))

(def create-article
  (ds/spec {:name :core/create-article
            :spec {:title            spec/non-empty-string?
                   :description      spec/non-empty-string?
                   :body             spec/non-empty-string?
                   (ds/opt :tagList) [spec/non-empty-string?]}}))

(def update-article
  (ds/spec {:name :core/update-article
            :spec {(ds/opt :title)       spec/non-empty-string?
                   (ds/opt :description) spec/non-empty-string?
                   (ds/opt :body)        spec/non-empty-string?}}))

(def article
  (ds/spec {:name :core/article
            :spec {:id               pos-int?
                   :slug             spec/slug?
                   :title            spec/non-empty-string?
                   :description      spec/non-empty-string?
                   :body             spec/non-empty-string?
                   :updatedAt        string?
                   :createdAt        string?
                   :favorited        boolean?
                   :favoritesCount   nat-int?
                   :author           profile-spec/profile
                   (ds/opt :tagList) [spec/non-empty-string?]}}))

(def visible-article
  (ds/spec {:name :core/visible-article
            :spec {:article article}}))
