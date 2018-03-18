(ns clojure.org.realworld.profile.interface
  (:require [clojure.org.realworld.profile.core :as core]))

(defn profile [auth-token username]
  (core/profile auth-token username))

(defn follow! [auth-token username]
  (core/follow! auth-token username))

(defn unfollow! [auth-token username]
  (core/unfollow! auth-token username))
