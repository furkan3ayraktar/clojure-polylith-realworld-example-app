(ns clojure.org.realworld.article.interface
  (:require [clojure.org.realworld.article.core :as core]))

(defn article [auth-user slug]
  (core/article auth-user slug))

(defn create-article! [auth-user article-input]
  (core/create-article! auth-user article-input))

(defn update-article! [auth-user slug article-input]
  (core/update-article! auth-user slug article-input))

(defn delete-article! [auth-user slug]
  (core/delete-article! auth-user slug))
