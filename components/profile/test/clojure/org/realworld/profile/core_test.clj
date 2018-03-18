(ns clojure.org.realworld.profile.core-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.common.interface]
            [clojure.org.realworld.database.interface :as database]
            [clojure.test :refer :all]
            [clojure.org.realworld.profile.core :as core]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn- test-db
  ([] {:classname   "org.sqlite.JDBC"
       :subprotocol "sqlite"
       :subname     "test.db"})
  ([_] (test-db)))

(defn prepare-for-tests [f]
  (with-redefs [database/db test-db]
    (let [db (test-db)]
      (database/generate-db db)
      (f)
      (database/drop-db db))))

(use-fixtures :each prepare-for-tests)

(deftest profile--profile-not-found--return-negative-result
  (let [[ok? res] (core/profile "token" "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a profile with given username."]}} res))))

(deftest profile--not-logged-in--return-positive-result-with-false-following
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        [ok? res] (core/profile "token" "username")]
    (is (true? ok?))
    (is (= {:profile {:username "username"
                      :bio "bio"
                      :image "image"
                      :following false}}
           res))))

(deftest profile--logged-in-not-following--return-positive-result-with-false-following
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        _ (jdbc/insert! (database/db) :user {:token "token"})
        [ok? res] (core/profile "token" "username")]
    (is (true? ok?))
    (is (= {:profile {:username "username"
                      :bio "bio"
                      :image "image"
                      :following false}}
           res))))

(deftest profile--logged-in-following--return-positive-result-with-true-following
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        _ (jdbc/insert! (database/db) :user {:token "token"})
        _ (jdbc/insert! (database/db) :userFollows {:userId 2 :followedUserId 1})
        [ok? res] (core/profile "token" "username")]
    (is (true? ok?))
    (is (= {:profile {:username "username"
                      :bio "bio"
                      :image "image"
                      :following true}}
           res))))

(deftest follow!--not-logged-in--return-negative-result
  (let [[ok? res] (core/follow! "token" "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a user with associated token."]}} res))))

(deftest follow!--profile-not-found--return-negative-result
  (let [_ (jdbc/insert! (database/db) :user {:token "token"})
        [ok? res] (core/follow! "token" "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a profile with given username."]}} res))))

(deftest follow!--logged-in-and-profile-found--return-positive-result
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        _ (jdbc/insert! (database/db) :user {:token "token"})
        [ok? res] (core/follow! "token" "username")]
    (is (true? ok?))
    (is (= {:profile {:username "username"
                      :bio "bio"
                      :image "image"
                      :following true}}
           res))))

(deftest unfollow!--not-logged-in--return-negative-result
  (let [[ok? res] (core/unfollow! "token" "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a user with associated token."]}} res))))

(deftest unfollow!--profile-not-found--return-negative-result
  (let [_ (jdbc/insert! (database/db) :user {:token "token"})
        [ok? res] (core/unfollow! "token" "username")]
    (is (false? ok?))
    (is (= {:errors {:username ["Cannot find a profile with given username."]}} res))))

(deftest unfollow!--logged-in-and-profile-found--return-positive-result
  (let [_ (jdbc/insert! (database/db) :user {:username "username"
                                             :bio      "bio"
                                             :image    "image"})
        _ (jdbc/insert! (database/db) :user {:token "token"})
        _ (jdbc/insert! (database/db) :userFollows {:userId 2 :followedUserId 1})
        [ok? res] (core/unfollow! "token" "username")]
    (is (true? ok?))
    (is (= {:profile {:username "username"
                      :bio "bio"
                      :image "image"
                      :following false}}
           res))))
