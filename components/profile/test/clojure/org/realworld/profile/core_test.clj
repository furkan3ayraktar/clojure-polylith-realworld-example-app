(ns clojure.org.realworld.profile.core-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [clojure.org.realworld.profile.core :as core]
            [clojure.org.realworld.user.interface :as user]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]))

(defn- test-db
  ([] {:classname   "org.sqlite.JDBC"
       :subprotocol "sqlite"
       :subname     "test.db"})
  ([_] (test-db)))

(def ^:private auth-user
  (assoc (gen/generate (s/gen user/user)) :id 1))

(defn prepare-for-tests [f]
  (with-redefs [database/db test-db]
    (let [db (test-db)]
      (database/generate-db db)
      (jdbc/insert! db :user auth-user)
      (f)
      (database/drop-db db))))

(use-fixtures :each prepare-for-tests)

(deftest fetch-profile--profile-not-found--return-negative-result
  (let [[ok? res] (core/fetch-profile auth-user "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a profile with given username."]}} res))))

(deftest fetch-profile--not-logged-in--return-positive-result-with-false-following
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        [ok? res] (core/fetch-profile nil "username")]
    (is (true? ok?))
    (is (= {:profile {:username  "username"
                      :bio       "bio"
                      :image     "image"
                      :following false}}
           res))))

(deftest fetch-profile--logged-in-not-following--return-positive-result-with-false-following
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        [ok? res] (core/fetch-profile auth-user "username")]
    (is (true? ok?))
    (is (= {:profile {:username  "username"
                      :bio       "bio"
                      :image     "image"
                      :following false}}
           res))))

(deftest fetch-profile--logged-in-following--return-positive-result-with-true-following
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        _ (jdbc/insert! (database/db) :userFollows {:userId 1 :followedUserId 2})
        [ok? res] (core/fetch-profile auth-user "username")]
    (is (true? ok?))
    (is (= {:profile {:username  "username"
                      :bio       "bio"
                      :image     "image"
                      :following true}}
           res))))

(deftest follow!--profile-not-found--return-negative-result
  (let [[ok? res] (core/follow! auth-user "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a profile with given username."]}} res))))

(deftest follow!--profile-found--return-positive-result
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        [ok? res] (core/follow! auth-user "username")]
    (is (true? ok?))
    (is (= {:profile {:username  "username"
                      :bio       "bio"
                      :image     "image"
                      :following true}}
           res))))

(deftest unfollow!--profile-not-found--return-negative-result
  (let [[ok? res] (core/unfollow! auth-user "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a profile with given username."]}} res))))

(deftest unfollow!--logged-in-and-profile-found--return-positive-result
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        _ (jdbc/insert! (database/db) :userFollows {:userId 1 :followedUserId 2})
        [ok? res] (core/unfollow! auth-user "username")]
    (is (true? ok?))
    (is (= {:profile {:username  "username"
                      :bio       "bio"
                      :image     "image"
                      :following false}}
           res))))
