(ns clojure.realworld.article.interface)

(def create-article)

(def update-article)

(defn article [auth-user slug])

(defn create-article! [auth-user article-input])

(defn update-article! [auth-user slug article-input])

(defn delete-article! [auth-user slug])

(defn favorite-article! [auth-user slug])

(defn unfavorite-article! [auth-user slug])

(defn feed [auth-user limit offset])

(defn articles [auth-user limit offset author tag favorited])
