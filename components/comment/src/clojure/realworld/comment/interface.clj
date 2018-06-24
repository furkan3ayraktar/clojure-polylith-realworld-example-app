(ns clojure.realworld.comment.interface
  (:require [clojure.realworld.comment.core :as core]
            [clojure.realworld.comment.spec :as spec]))

(def id spec/id)

(def add-comment spec/add-comment)

(defn article-comments [auth-user slug]
  (core/article-comments auth-user slug))

(defn add-comment! [auth-user slug comment]
  (core/add-comment! auth-user slug comment))

(defn delete-comment! [auth-user id]
  (core/delete-comment! auth-user id))
