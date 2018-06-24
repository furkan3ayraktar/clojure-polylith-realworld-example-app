(ns clojure.realworld.rest-api.handler
  (:require [clojure.realworld.article.interface :as article]
            [clojure.realworld.comment.interface :as comment-comp]
            [clojure.realworld.spec.interface :as spec]
            [clojure.realworld.profile.interface :as profile]
            [clojure.realworld.tag.interface :as tag]
            [clojure.realworld.user.interface :as user]
            [clojure.spec.alpha :as s]))

(defn- parse-query-param [param]
  (if (string? param)
    (try
      (read-string param)
      (catch Exception _
        param))
    param))

(defn- handle
  ([status body]
   {:status (or status 404)
    :body   body})
  ([status]
   (handle status nil)))

(defn options [_]
  (handle 200))

(defn other [_]
  (handle 404 {:errors {:other ["Route not found."]}}))

(defn login [req]
  (let [user (-> req :params :user)]
    (if (s/valid? user/login user)
      (let [[ok? res] (user/login! user)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:body ["Invalid request body."]}}))))

(defn register [req]
  (let [user (-> req :params :user)]
    (if (s/valid? user/register user)
      (let [[ok? res] (user/register! user)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:body ["Invalid request body."]}}))))

(defn current-user [req]
  (let [auth-user (-> req :auth-user)]
    (handle 200 {:user auth-user})))

(defn update-user [req]
  (let [auth-user (-> req :auth-user)
        user      (-> req :params :user)]
    (if (s/valid? user/update-user user)
      (let [[ok? res] (user/update-user! auth-user user)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:body ["Invalid request body."]}}))))

(defn profile [req]
  (let [auth-user (-> req :auth-user)
        username  (-> req :params :username)]
    (if (s/valid? spec/username? username)
      (let [[ok? res] (profile/fetch-profile auth-user username)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:username ["Invalid username."]}}))))

(defn follow-profile [req]
  (let [auth-user (-> req :auth-user)
        username  (-> req :params :username)]
    (if (s/valid? spec/username? username)
      (let [[ok? res] (profile/follow! auth-user username)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:username ["Invalid username."]}}))))

(defn unfollow-profile [req]
  (let [auth-user (-> req :auth-user)
        username  (-> req :params :username)]
    (if (s/valid? spec/username? username)
      (let [[ok? res] (profile/unfollow! auth-user username)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:username ["Invalid username."]}}))))

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
    (handle (if ok? 200 404) res)))

(defn article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? spec/slug? slug)
      (let [[ok? res] (article/article auth-user slug)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:slug ["Invalid slug."]}}))))

(defn comments [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? spec/slug? slug)
      (let [[ok? res] (comment-comp/article-comments auth-user slug)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:slug ["Invalid slug."]}}))))

(defn tags [_]
  (let [[ok? res] (tag/all-tags)]
    (handle (if ok? 200 404) res)))

(defn feed [req]
  (let [auth-user (-> req :auth-user)
        limit     (parse-query-param (-> req :params :limit))
        offset    (parse-query-param (-> req :params :offset))
        [ok? res] (article/feed auth-user
                                (if (pos-int? limit) limit nil)
                                (if (nat-int? offset) offset nil))]
    (handle (if ok? 200 404) res)))

(defn create-article [req]
  (let [auth-user (-> req :auth-user)
        article   (-> req :params :article)]
    (if (s/valid? article/create-article article)
      (let [[ok? res] (article/create-article! auth-user article)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:body ["Invalid request body."]}}))))

(defn update-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)
        article   (-> req :params :article)]
    (if (and (s/valid? article/update-article article)
             (s/valid? spec/slug? slug))
      (let [[ok? res] (article/update-article! auth-user slug article)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:body ["Invalid request body."]}}))))

(defn delete-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? spec/slug? slug)
      (let [[ok? res] (article/delete-article! auth-user slug)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:slug ["Invalid slug."]}}))))

(defn add-comment [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)
        comment   (-> req :params :comment)]
    (if (and (s/valid? spec/slug? slug)
             (s/valid? comment-comp/add-comment comment))
      (let [[ok? res] (comment-comp/add-comment! auth-user slug comment)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:body ["Invalid request body."]}}))))

(defn delete-comment [req]
  (let [auth-user (-> req :auth-user)
        id        (parse-query-param (-> req :params :id))]
    (if (s/valid? comment-comp/id id)
      (let [[ok? res] (comment-comp/delete-comment! auth-user id)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:id ["Invalid comment id."]}}))))

(defn favorite-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? spec/slug? slug)
      (let [[ok? res] (article/favorite-article! auth-user slug)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:slug ["Invalid slug."]}}))))

(defn unfavorite-article [req]
  (let [auth-user (-> req :auth-user)
        slug      (-> req :params :slug)]
    (if (s/valid? spec/slug? slug)
      (let [[ok? res] (article/unfavorite-article! auth-user slug)]
        (handle (if ok? 200 404) res))
      (handle 422 {:errors {:slug ["Invalid slug."]}}))))
