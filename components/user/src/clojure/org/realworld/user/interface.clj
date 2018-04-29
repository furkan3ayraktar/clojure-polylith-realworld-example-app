(ns clojure.org.realworld.user.interface
  (:require [clojure.org.realworld.user.core :as core]
            [clojure.org.realworld.user.spec :as spec]
            [clojure.org.realworld.user.store :as store]))

(def login spec/login)

(def register spec/register)

(def update-user spec/update-user)

(def user spec/user)

(defn login! [login-input]
  (core/login! login-input))

(defn register! [register-input]
  (core/register! register-input))

(defn user-by-token [token]
  (core/user-by-token token))

(defn update-user! [auth-user user-input]
  (core/update-user! auth-user user-input))

(defn find-by-username-or-id [username-or-id]
  (store/find-by-username-or-id username-or-id))
