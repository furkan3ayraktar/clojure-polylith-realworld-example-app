(ns clojure.realworld.user.spec
  (:require [clojure.realworld.spec.interface :as spec]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]))

(def id
  (st/spec {:spec        pos-int?
            :type        :long
            :description "A long spec that defines a user id which is a positive integer"}))

(def login
  (ds/spec {:name :core/login
            :spec {:email    spec/email?
                   :password spec/password?}}))

(def register
  (ds/spec {:name :core/register
            :spec {:username spec/username?
                   :email    spec/email?
                   :password spec/password?}}))

(def update-user
  (ds/spec {:name         :core/update-user
            :spec         {:email    spec/email?
                           :username spec/username?
                           :password spec/password?
                           :image    (ds/maybe spec/uri-string?)
                           :bio      (ds/maybe spec/non-empty-string?)}
            :keys-default ds/opt}))

(def user
  (ds/spec {:name :core/user
            :spec {:id             id
                   :email          spec/email?
                   :username       spec/username?
                   (ds/opt :image) (ds/maybe spec/uri-string?)
                   (ds/opt :bio)   (ds/maybe spec/non-empty-string?)
                   (ds/opt :token) spec/non-empty-string?}}))

(def visible-user
  (ds/spec {:name :core/visible-user
            :spec {:user user}}))
