(ns clojure.realworld.profile.store-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [clojure.realworld.profile.store :as store]
            [clojure.test :refer :all]))

(defn test-db
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

(deftest following?--following--return-true
  (let [_ (jdbc/insert! (test-db) :userFollows {:userId         1
                                                :followedUserId 2})
        res (store/following? 1 2)]
    (is (true? res))))

(deftest following?--not-following--return-false
  (let [res (store/following? 1 2)]
    (is (false? res))))

(deftest follow!--currently-not-following--insert-user-follows
  (let [before-following? (store/following? 1 2)
        _ (store/follow! 1 2)
        after-following? (store/following? 1 2)]
    (is (false? before-following?))
    (is (true? after-following?))))

(deftest follow!--currently-following--do-nothing
  (let [_ (store/follow! 1 2)
        before-following? (store/following? 1 2)
        _ (store/follow! 1 2)
        after-following? (store/following? 1 2)]
    (is (true? before-following?))
    (is (true? after-following?))))

(deftest unfollow!--currently-following--delete-user-follows
  (let [_ (store/follow! 1 2)
        before-following? (store/following? 1 2)
        _ (store/unfollow! 1 2)
        after-following? (store/following? 1 2)]
    (is (true? before-following?))
    (is (false? after-following?))))

(deftest unfollow!--currently-not-following--do-nothing
  (let [before-following? (store/following? 1 2)
        _ (store/unfollow! 1 2)
        after-following? (store/following? 1 2)]
    (is (false? before-following?))
    (is (false? after-following?))))
