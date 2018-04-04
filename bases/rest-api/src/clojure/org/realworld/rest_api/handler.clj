(ns clojure.org.realworld.rest-api.handler
  (:require [clojure.org.realworld.article.interface :as article]
            [clojure.org.realworld.comment.interface :as comments]
            [clojure.org.realworld.spec.interface]
            [clojure.org.realworld.profile.interface :as profile]
            [clojure.org.realworld.tag.interface :as tag]
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
  (let [auth-user (-> req :auth-user)]
    (handler 200 {:user auth-user})))

(defn update-user [req]
  (let [auth-user (-> req :auth-user)
        user      (-> req :params :user)]
    (if (s/valid? :core/update-user user)
      (let [[ok? res] (user/update-user! auth-user user)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn profile [req]
  (let [auth-user (-> req :auth-user)
        username  (-> req :params :username)]
    (if (s/valid? :core/username username)
      (let [[ok? res] (profile/profile auth-user username)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:username ["Invalid username."]}}))))

(defn follow-profile [req]
  (let [auth-user (-> req :auth-user)
        username  (-> req :params :username)]
    (if (s/valid? :core/username username)
      (let [[ok? res] (profile/follow! auth-user username)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:username ["Invalid username."]}}))))

(defn unfollow-profile [req]
  (let [auth-user (-> req :auth-user)
        username  (-> req :params :username)]
    (if (s/valid? :core/username username)
      (let [[ok? res] (profile/unfollow! auth-user username)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:username ["Invalid username."]}}))))

(defn articles [req]
  (let [auth-user (-> req :auth-user)
        limit     (parse-query-param (-> req :params :limit))
        offset    (parse-query-param (-> req :params :offset))
        author    (-> req :params :author)
        tag       (-> req :params :tag)
        favorited (-> req :params :favorited)
        [ok? res] (article/articles auth-user
                                    (if (pos-int? limit) limit nil)
                                    (if (nat-int? offset) offset nil)
                                    (if (string? author) author nil)
                                    (if (string? tag) tag nil)
                                    (if (string? favorited) favorited nil))]
    (handler (if ok? 200 404) res)))

(defn article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? :core/slug slug)
      (let [[ok? res] (article/article auth-user slug)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:slug ["Invalid slug."]}}))))

(defn comments [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? :core/slug slug)
      (let [[ok? res] (comments/article-comments auth-user slug)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:slug ["Invalid slug."]}}))))

(defn tags [_]
  (let [[ok? res] (tag/all-tags)]
    (handler (if ok? 200 404) res)))

(defn feed [req]
  (let [auth-user (-> req :auth-user)
        limit     (parse-query-param (-> req :params :limit))
        offset    (parse-query-param (-> req :params :offset))
        [ok? res] (article/feed auth-user
                                (if (pos-int? limit) limit nil)
                                (if (nat-int? offset) offset nil))]
    (handler (if ok? 200 404) res)))

(defn create-article [req]
  (let [auth-user (-> req :auth-user)
        article   (-> req :params :article)]
    (if (s/valid? :core/create-article article)
      (let [[ok? res] (article/create-article! auth-user article)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn update-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)
        article   (-> req :params :article)]
    (if (and (s/valid? :core/update-article article)
             (s/valid? :core/slug slug))
      (let [[ok? res] (article/update-article! auth-user slug article)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn delete-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? :core/slug slug)
      (let [[ok? res] (article/delete-article! auth-user slug)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:slug ["Invalid slug."]}}))))

(defn add-comment [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)
        comment   (-> req :params :comment)]
    (if (and (s/valid? :core/slug slug)
             (s/valid? :core/add-comment comment))
      (let [[ok? res] (comments/add-comment! auth-user slug comment)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:body ["Invalid request body."]}}))))

(defn delete-comment [req]
  (let [auth-user (-> req :auth-user)
        id        (parse-query-param (-> req :params :id))]
    (if (s/valid? :comment/id id)
      (let [[ok? res] (comments/delete-comment! auth-user id)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:id ["Invalid comment id."]}}))))

(defn favorite-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? :core/slug slug)
      (let [[ok? res] (article/favorite-article! auth-user slug)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:slug ["Invalid slug."]}}))))

(defn unfavorite-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? :core/slug slug)
      (let [[ok? res] (article/unfavorite-article! auth-user slug)]
        (handler (if ok? 200 404) res))
      (handler 422 {:errors {:slug ["Invalid slug."]}}))))
