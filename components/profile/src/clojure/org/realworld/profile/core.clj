(ns clojure.org.realworld.profile.core
  (:require [clojure.org.realworld.profile.store :as store]
            [clojure.org.realworld.user.interface :as user]))

(defn- create-profile [user following?]
  (let [profile (assoc (select-keys user [:username :bio :image])
                  :following following?)]
    {:profile profile}))

(defn profile [auth-user username]
  (let [user (user/find-by-username username)]
    (if (nil? user)
      [false {:errors {:username ["Cannot find a profile with given username."]}}]
      (let [following? (if (nil? auth-user)
                         false
                         (store/following? (:id auth-user) (:id user)))]
        [true (create-profile user following?)]))))

(defn follow! [auth-user username]
  (if-let [user (user/find-by-username username)]
    (do
      (store/follow! (:id auth-user) (:id user))
      [true (create-profile user true)])
    [false {:errors {:username ["Cannot find a profile with given username."]}}]))

(defn unfollow! [auth-user username]
  (if-let [user (user/find-by-username username)]
    (do
      (store/unfollow! (:id auth-user) (:id user))
      [true (create-profile user false)])
    [false {:errors {:username ["Cannot find a profile with given username."]}}]))
