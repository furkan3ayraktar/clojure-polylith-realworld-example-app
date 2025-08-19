(ns clojure.realworld.spec.interface
  (:require [clojure.realworld.spec.core :as core]
            [clojure.realworld.spec.user :as user]))

(def non-empty-string? core/non-empty-string?)

(def email? core/email?)

(def uri-string? core/uri-string?)

(def slug? core/slug?)

(def password? core/password?)

;; User specs
(def username? user/username?)

(def id user/id)

(def login user/login)

(def register user/register)

(def update-user user/update-user)

(def user user/user)

(def visible-user user/visible-user)
