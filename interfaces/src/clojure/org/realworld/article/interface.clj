(ns clojure.org.realworld.article.interface)

(defn article [auth-user slug])

(defn create-article! [auth-user article-input])

(defn update-article! [auth-user slug article-input])

(defn delete-article! [auth-user slug])

(defn favorite-article! [auth-user slug])

(defn unfavorite-article! [auth-user slug])
