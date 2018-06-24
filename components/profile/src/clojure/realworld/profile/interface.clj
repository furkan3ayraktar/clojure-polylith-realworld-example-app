(ns clojure.realworld.profile.interface
  (:require [clojure.realworld.profile.core :as core]
            [clojure.realworld.profile.spec :as spec]))

(def profile spec/profile)

(defn fetch-profile [auth-user username]
  (core/fetch-profile auth-user username))

(defn follow! [auth-user username]
  (core/follow! auth-user username))

(defn unfollow! [auth-user username]
  (core/unfollow! auth-user username))
