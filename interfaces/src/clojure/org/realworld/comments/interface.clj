(ns clojure.org.realworld.comments.interface)

(defn article-comments [auth-user slug])

(defn add-comment! [auth-user slug comment])

(defn delete-comment! [auth-user id])
