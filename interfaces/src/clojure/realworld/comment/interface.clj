(ns clojure.realworld.comment.interface)

(def id)

(def add-comment)

(defn article-comments [auth-user slug])

(defn add-comment! [auth-user slug comment])

(defn delete-comment! [auth-user id])
