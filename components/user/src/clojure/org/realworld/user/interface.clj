(ns clojure.org.realworld.user.interface
  (:require [clojure.org.realworld.user.core :as core]))

(defn login [login-input]
  (core/login login-input))

(defn register! [register-input]
  (core/register! register-input))

(defn user-by-token [token]
  (core/user-by-token token))

(defn update-user! [auth-token user-input]
  (core/update-user! auth-token user-input))
