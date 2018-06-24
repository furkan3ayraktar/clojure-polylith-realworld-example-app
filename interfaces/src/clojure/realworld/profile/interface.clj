(ns clojure.realworld.profile.interface)

(def profile)

(defn fetch-profile [auth-user username])

(defn follow! [auth-user username])

(defn unfollow! [auth-user username])
