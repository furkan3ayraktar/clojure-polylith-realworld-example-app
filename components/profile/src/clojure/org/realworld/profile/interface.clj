(ns clojure.org.realworld.profile.interface
  (:require [clojure.org.realworld.profile.core :as core]))

(defn profile [auth-user username]
  (core/profile auth-user username))

(defn follow! [auth-user username]
  (core/follow! auth-user username))

(defn unfollow! [auth-user username]
  (core/unfollow! auth-user username))
