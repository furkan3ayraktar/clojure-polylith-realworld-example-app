(ns clojure.org.realworld.comments.interface
  (:require [clojure.org.realworld.comments.core :as core]))

(defn article-comments [auth-user slug]
  (core/article-comments auth-user slug))

(defn add-comment! [auth-user slug comment]
  (core/add-comment! auth-user slug comment))

(defn delete-comment! [auth-user id]
  (core/delete-comment! auth-user id))
