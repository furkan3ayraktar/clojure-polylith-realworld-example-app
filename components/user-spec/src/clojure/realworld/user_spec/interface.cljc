(ns clojure.realworld.user-spec.interface
  (:require [clojure.realworld.user-spec.spec :as spec]))

(def id spec/id)

(def login spec/login)

(def register spec/register)

(def update-user spec/update-user)

(def user spec/user)

(def visible-user spec/visible-user)
