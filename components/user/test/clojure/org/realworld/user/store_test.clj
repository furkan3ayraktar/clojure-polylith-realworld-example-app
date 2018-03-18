(ns clojure.org.realworld.user.store-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [clojure.org.realworld.user.store :as store]
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

(deftest find-by-key--test
  (let [_    (jdbc/insert! (test-db) :user {:email    "test@test.com"
                                            :username "username"})
        res1 (store/find-by :email "test@test.com")
        user {:bio      nil
              :email    "test@test.com"
              :id       1
              :image    nil
              :password nil
              :token    nil
              :username "username"}
        res2 (store/find-by :username "username")]
    (is (= user res1))
    (is (= user res2))))

(deftest update-token!--test
  (let [_    (jdbc/insert! (test-db) :user {:email "test@test.com"})
        res1 (store/find-by-email "test@test.com")
        _    (store/update-token! "test@test.com" "new-token")
        res2 (store/find-by-email "test@test.com")]
    (is (nil? (:token res1)))
    (is (= "new-token" (:token res2)))))

(deftest insert-user!--test
  (let [user {:bio      "bio"
              :email    "test@test.com"
              :image    "image"
              :password "password"
              :token    "token"
              :username "username"}
        _    (store/insert-user! user)
        res  (store/find-by-email "test@test.com")]
    (is (= (assoc user :id 1) res))))

(deftest update-user!--test
  (let [_    (store/insert-user! {:bio      "bio"
                                  :email    "test@test.com"
                                  :image    "image"
                                  :password "password"
                                  :token    "token"
                                  :username "username"})
        user {:bio      "updated-bio"
              :email    "updated-test@test.com"
              :image    "updated-image"
              :password "updated-password"
              :token    "updated-token"
              :username "updated-username"}
        _    (store/update-user! 1 user)
        res  (store/find-by-email "updated-test@test.com")]
    (is (= (assoc user :id 1) res))))
