(ns clojure.org.realworld.profile.interface)

(defn profile [auth-token username])

(defn follow! [auth-token username])

(defn unfollow! [auth-token username])
