(ns clojure.realworld.user.core-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [clojure.realworld.user.core :as core]
            [clojure.realworld.user.spec :as spec]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]))

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

(deftest login--user-not-found--return-negative-result
  (let [[ok? res] (core/login! {:email "test@test.com" :password "password"})]
    (is (false? ok?))
    (is (= {:errors {:email ["Invalid email."]}} res))))

(deftest login--invalid-password--return-negative-result
  (let [_ (jdbc/insert! (test-db) :user {:email    "test@test.com"
                                         :password (core/encrypt-password "password")})
        [ok? res] (core/login! {:email "test@test.com" :password "invalid-password"})]
    (is (false? ok?))
    (is (= {:errors {:password ["Invalid password."]}} res))))

(deftest login--valid-input--return-positive-result
  (let [_ (jdbc/insert! (test-db) :user {:email    "test@test.com"
                                         :username "username"
                                         :password (core/encrypt-password "password")})
        [ok? res] (core/login! {:email "test@test.com" :password "password"})]
    (is (true? ok?))
    (is (true? (s/valid? spec/visible-user res)))))

(deftest register!--user-exists-with-given-email--return-negative-result
  (let [_ (jdbc/insert! (test-db) :user {:email "test@test.com"})
        [ok? res] (core/register! {:email "test@test.com"})]
    (is (false? ok?))
    (is (= {:errors {:email ["A user exists with given email."]}} res))))

(deftest register!--user-exists-with-given-username--return-negative-result
  (let [_ (jdbc/insert! (test-db) :user {:username "username"})
        [ok? res] (core/register! {:email "test@test.com" :username "username"})]
    (is (false? ok?))
    (is (= {:errors {:username ["A user exists with given username."]}} res))))

(deftest register!--valid-input--return-positive-result
  (let [input (gen/generate (s/gen spec/register))
        [ok? res] (core/register! input)]
    (is (true? ok?))
    (is (s/valid? spec/visible-user res))
    (is (not (nil? (-> res :user :token))))))

(deftest user-by-token--user-not-found--return-negative-result
  (let [[ok? res] (core/user-by-token "token")]
    (is (false? ok?))
    (is (= {:errors {:token ["Cannot find a user with associated token."]}} res))))

(deftest user-by-token--user-found--return-positive-result
  (let [_ (jdbc/insert! (test-db) :user {:email "test@test.com" :token "token" :username "username"})
        [ok? res] (core/user-by-token "token")]
    (is (true? ok?))
    (is (s/valid? spec/visible-user res))))

(deftest update-user!--user-exists-with-given-email--return-negative-result
  (let [_ (jdbc/insert! (test-db) :user {:email "test1@test.com"})
        auth-user (jdbc/insert! (test-db) :user {:email "test2@test.com" :token "token"})
        [ok? res] (core/update-user! auth-user {:email "test1@test.com"})]
    (is (false? ok?))
    (is (= {:errors {:email ["A user exists with given email."]}} res))))

(deftest update-user!--user-exists-with-given-username--return-negative-result
  (let [_ (jdbc/insert! (test-db) :user {:username "username"})
        auth-user (jdbc/insert! (test-db) :user {:email "test2@test.com" :token "token"})
        [ok? res] (core/update-user! auth-user {:email "test@test.com" :username "username" :token "token"})]
    (is (false? ok?))
    (is (= {:errors {:username ["A user exists with given username."]}} res))))

(deftest update-user!--valid-input--return-positive-result
  (let [initial-inputs (gen/sample (s/gen spec/register) 20)
        users (map #(-> (core/register! %) second :user) initial-inputs)
        inputs (gen/sample (s/gen spec/update-user) 20)
        results (map-indexed #(core/update-user! (nth users %1) %2) inputs)]
    (is (every? true? (map first results)))
    (is (every? #(s/valid? spec/visible-user (second %)) results))
    (is (= (map #(dissoc % :password) inputs)
           (map-indexed #(select-keys (-> %2 second :user)
                                      (keys (nth inputs %1)))
                        results)))))
