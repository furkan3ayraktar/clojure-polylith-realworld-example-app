(ns clojure.realworld.user.interface.spec
  (:require [clojure.realworld.user.spec :as spec]))

(def login spec/login)

(def register spec/register)

(def update-user spec/update-user)

(def user spec/user)

(def visible-user spec/visible-user)
