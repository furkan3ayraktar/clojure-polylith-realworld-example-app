(ns clojure.org.realworld.article.core-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.common.interface]
            [clojure.org.realworld.database.interface :as database]
            [clojure.org.realworld.article.core :as core]
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

(deftest article--article-not-found--return-negative-result
  (let [[ok? res] (core/article nil "wrong-slug")]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest article--article-found--return-positive-result
  (let [input {:title "title" :slug "this-is-slug"}
        _ (jdbc/insert! (test-db) :article input)
        [ok? res] (core/article nil "this-is-slug")]
    (is (true? ok?))
    (is (= 1 (-> res :article :id)))
    (is (= "title" (-> res :article :title)))
    (is (= "this-is-slug" (-> res :article :slug)))))

(deftest create-article--test
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        inputs (gen/sample (s/gen :core/create-article) 20)
        results (map #(core/create-article! auth-user %) inputs)]
    (is (every? true? (map first results)))
    (is (every? #(s/valid? :core/visible-article (second %)) results))))

(deftest update-article--article-not-found--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        input (gen/generate (s/gen :core/update-article))
        [ok? res] (core/update-article! auth-user "slug" input)]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest update-article--article-is-not-owned-by-user--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        initial (gen/generate (s/gen :core/create-article))
        [_ article] (core/create-article! auth-user initial)
        input (gen/generate (s/gen :core/update-article))
        [ok? res] (core/update-article! (assoc auth-user :id 2)
                                        (-> article :article :slug)
                                        input)]
    (is (false? ok?))
    (is (= {:errors {:authorization ["You need to be author of this article to update it."]}} res))))

(deftest update-article--input-is-ok--update-article-and-return-positive-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        initial-inputs (gen/sample (s/gen :core/create-article) 20)
        create-res (map #(core/create-article! auth-user %) initial-inputs)
        inputs (gen/sample (s/gen :core/update-article) 20)
        update-res (map-indexed #(core/update-article! auth-user
                                                      (-> (nth create-res %1) second :article :slug)
                                                      %2)
                                inputs)]
    (is (every? true? (map first update-res)))
    (is (every? #(s/valid? :core/visible-article (second %)) update-res))))

(deftest delete-article--article-not-found--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        [ok? res] (core/delete-article! auth-user "slug")]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest delete-article--article-is-not-owned-by-user--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        initial (gen/generate (s/gen :core/create-article))
        [_ article] (core/create-article! auth-user initial)
        [ok? res] (core/delete-article! (assoc auth-user :id 2)
                                        (-> article :article :slug))]
    (is (false? ok?))
    (is (= {:errors {:authorization ["You need to be author of this article to delete it."]}} res))))

(deftest delete-article--input-is-ok--delete-article-and-return-positive-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _ (jdbc/insert! (test-db) :user auth-user)
        initial-inputs (gen/sample (s/gen :core/create-article) 20)
        create-res (map #(core/create-article! auth-user %) initial-inputs)
        update-res (map #(core/delete-article! auth-user (-> % second :article :slug)) create-res)]
    (is (every? #(= [true nil] %) update-res))))
