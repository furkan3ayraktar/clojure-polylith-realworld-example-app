(ns clojure.realworld.spec.user
  (:require [clojure.realworld.spec.core :as core]
            [clojure.spec.gen.alpha :as gen]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]))

(def username?
  (st/spec {:spec        core/non-empty-string?
            :type        :string
            :description "A non empty string spec with a special username (UUID) generator."
            :gen         #(gen/fmap (fn [_] (str (random-uuid)))
                                    (gen/string-alphanumeric))}))

(def id
  (st/spec {:spec        pos-int?
            :type        :long
            :description "A long spec that defines a user id which is a positive integer"}))

(def login
  (ds/spec {:name :core/login
            :spec {:email    core/email?
                   :password core/password?}}))

(def register
  (ds/spec {:name :core/register
            :spec {:username username?
                   :email    core/email?
                   :password core/password?}}))

(def update-user
  (ds/spec {:name         :core/update-user
            :spec         {:email    core/email?
                           :username username?
                           :password core/password?
                           :image    (ds/maybe core/uri-string?)
                           :bio      (ds/maybe core/non-empty-string?)}
            :keys-default ds/opt}))

(def user-base
  {:id             id
   :email          core/email?
   :username       username?
   (ds/opt :image) (ds/maybe core/uri-string?)
   (ds/opt :bio)   (ds/maybe core/non-empty-string?)})

(def user
  (ds/spec {:name :core/user
            :spec user-base}))

(def visible-user
  (ds/spec {:name :core/visible-user
            :spec {:user (assoc user-base
                           (ds/opt :token) core/non-empty-string?)}}))
