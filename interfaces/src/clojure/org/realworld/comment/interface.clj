(ns clojure.org.realworld.comment.interface)

(defn article-comments [auth-user slug])

(defn add-comment! [auth-user slug comment])

(defn delete-comment! [auth-user id])
