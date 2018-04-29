(ns clojure.org.realworld.user.interface)

(def login)

(def register)

(def update-user)

(def user)

(defn login! [login-input])

(defn register! [register-input])

(defn user-by-token [token])

(defn update-user! [auth-user user-input])

(defn find-by-username-or-id [username-or-id])
