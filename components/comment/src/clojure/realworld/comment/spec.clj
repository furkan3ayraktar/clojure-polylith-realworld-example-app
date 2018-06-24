(ns clojure.realworld.comment.spec
  (:require [clojure.realworld.spec.interface :as spec]
            [clojure.realworld.profile.interface :as profile]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]))

(def id
  (st/spec {:spec        pos-int?
            :type        :long
            :description "A long spec that defines a comment id which is a positive integer"}))

(def add-comment
  (ds/spec {:name :core/add-comment
            :spec {:body spec/non-empty-string?}}))

(def comment-spec
  (ds/spec {:name :core/comment
            :spec {:id        pos-int?
                   :updatedAt string?
                   :createdAt string?
                   :body      spec/non-empty-string?
                   :author    profile/profile}}))

(def visible-comment
  (ds/spec {:name :core/visible-comment
            :spec {:comment comment-spec}}))
