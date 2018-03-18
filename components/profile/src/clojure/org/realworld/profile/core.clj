(ns clojure.org.realworld.profile.core
  (:require [clojure.org.realworld.profile.store :as store]
            [clojure.org.realworld.user.interface :as user]))

(defn- create-profile [user following?]
  (let [profile (assoc (select-keys user [:username :bio :image])
                  :following following?)]
    {:profile profile}))

(defn profile [auth-token username]
  (let [current-user (user/find-by-token auth-token)
        user         (user/find-by-username username)]
    (if (nil? user)
      [false {:errors {:username ["Cannot find a profile with given username."]}}]
      (let [following? (if (nil? current-user)
                         false
                         (store/following? (:id current-user) (:id user)))]
        [true (create-profile user following?)]))))

(defn follow! [auth-token username]
  (if-let [current-user (user/find-by-token auth-token)]
    (if-let [user (user/find-by-username username)]
      (do
        (store/follow! (:id current-user) (:id user))
        [true (create-profile user true)])
      [false {:errors {:username ["Cannot find a profile with given username."]}}])
    [false {:errors {:username ["Cannot find a user with associated token."]}}]))

(defn unfollow! [auth-token username]
  (if-let [current-user (user/find-by-token auth-token)]
    (if-let [user (user/find-by-username username)]
      (do
        (store/unfollow! (:id current-user) (:id user))
        [true (create-profile user false)])
      [false {:errors {:username ["Cannot find a profile with given username."]}}])
    [false {:errors {:username ["Cannot find a user with associated token."]}}]))
