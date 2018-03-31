(ns clojure.org.realworld.user.interface
  (:require [clojure.org.realworld.user.core :as core]
            [clojure.org.realworld.user.store :as store]))

(defn login [login-input]
  (core/login login-input))

(defn register! [register-input]
  (core/register! register-input))

(defn user-by-token [token]
  (core/user-by-token token))

(defn update-user! [auth-user user-input]
  (core/update-user! auth-user user-input))

(defn find-by-username [username]
  (store/find-by-username username))
