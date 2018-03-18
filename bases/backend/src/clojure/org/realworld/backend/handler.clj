(ns clojure.org.realworld.backend.handler
  (:require [clojure.org.realworld.article.interface :as article]
            [clojure.org.realworld.common.spec]
            [clojure.org.realworld.profile.interface :as profile]
            [clojure.org.realworld.tags.interface :as tags]
            [clojure.org.realworld.user.interface :as user]
            [clojure.spec.alpha :as s]))

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
  (handler 404 {:errors {:other ["Route not found."]}}))

(defn login [req]
  (let [user (-> req :params :user)]
    (if (s/valid? :core/login user)
      (let [[ok? res] (user/login user)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn register [req]
  (let [user (-> req :params :user)]
    (if (s/valid? :core/register user)
      (let [[ok? res] (user/register! user)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn current-user [req]
  (let [auth-token (-> req :auth-token)
        [ok? res] (user/user-by-token auth-token)]
    (handler (if ok? 200 404) res)))

(defn update-user [req]
  (let [auth-token (-> req :auth-token)
        user (-> req :params :user)]
    (if (s/valid? :core/update-user user)
      (let [[ok? res] (user/update-user! auth-token user)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn profile [req]
  (handler 200))

(defn follow-profile [req]
  (handler 200))

(defn unfollow-profile [req]
  (handler 200))

(defn articles [req]
  (handler 200))

(defn article [req]
  (handler 200))

(defn comments [req]
  (handler 200))

(defn tags [req]
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
