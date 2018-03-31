(ns clojure.org.realworld.profile.interface)

(defn profile [auth-user username])

(defn follow! [auth-user username])

(defn unfollow! [auth-user username])
