(ns clojure.org.realworld.backend.handler
  (:require [clojure.org.realworld.article.interface :as article]
            [clojure.org.realworld.profile.interface :as profile]
            [clojure.org.realworld.tags.interface :as tags]
            [clojure.org.realworld.user.interface :as user]))

(defn- parse-query-param [param]
  (if (string? param)
    (try
      (read-string param)
      (catch Exception _
        param))
    param))

(defn- handler
  ([status body]
   {:status (or status 404)
    :body   body})
  ([status]
   (handler status nil)))

(defn options [_]
  (handler 200))

(defn other [_]
  (handler 404 {:message "Route not found."}))

(defn login [req]
  (handler 200))

(defn register [req]
  (handler 200))

(defn profile [req]
  (handler 200))

(defn articles [req]
  (handler 200))

(defn article [req]
  (handler 200))

(defn comments [req]
  (handler 200))

(defn tags [req]
  (handler 200))

(defn current-user [req]
  (handler 200))

(defn update-user [req]
  (handler 200))

(defn follow-profile [req]
  (handler 200))

(defn unfollow-profile [req]
  (handler 200))

(defn feed [req]
  (handler 200))

(defn create-article [req]
  (handler 200))

(defn update-article [req]
  (handler 200))

(defn delete-article [req]
  (handler 200))

(defn add-comment [req]
  (handler 200))

(defn delete-comment [req]
  (handler 200))

(defn favorite-article [req]
  (handler 200))

(defn unfavorite-article [req]
  (handler 200))
