(ns clojure.org.realworld.comment.core-test
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.comment.core :as core]
            [clojure.org.realworld.database.interface :as database]
            [clojure.org.realworld.user.spec :as user-spec]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]))

(defn test-db
  ([] {:classname   "org.sqlite.JDBC"
       :subprotocol "sqlite"
       :subname     "test.db"})
  ([_] (test-db)))

(def ^:private auth-user
  (assoc (gen/generate (s/gen user-spec/user)) :id 1))

(defn prepare-for-tests [f]
  (with-redefs [database/db test-db]
    (let [db (test-db)]
      (database/generate-db db)
      (jdbc/insert! db :user auth-user)
      (f)
      (database/drop-db db))))

(use-fixtures :each prepare-for-tests)

(deftest article-comments--article-not-found--return-negative-response
  (let [[ok? res] (core/article-comments auth-user "slug")]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest article-comments--no-comments-found--return-positive-response-with-empty-vector
  (let [_ (jdbc/insert! (test-db) :article {:slug "slug"})
        [ok? res] (core/article-comments auth-user "slug")]
    (is (true? ok?))
    (is (= {:comments []} res))))

(deftest article-comments--comments-found--return-positive-response
  (let [_ (jdbc/insert! (test-db) :article {:slug "slug"})
        _ (jdbc/insert-multi! (test-db) :user (map-indexed #(assoc %2 :id (+ 2 %1)) (gen/sample (s/gen user-spec/user) 2)))
        _ (jdbc/insert-multi! (test-db) :comment [{:body "body1" :articleId 1 :userId 1}
                                                  {:body "body2" :articleId 1 :userId 2}
                                                  {:body "body3" :articleId 1 :userId 2}
                                                  {:body "body4" :articleId 1 :userId 3}])
        [ok? res] (core/article-comments auth-user "slug")]
    (is (true? ok?))
    (is (= 4 (-> res :comments count)))))

(deftest article-comments--comments-found-without-auth--return-positive-response
  (let [_ (jdbc/insert! (test-db) :article {:slug "slug"})
        _ (jdbc/insert-multi! (test-db) :user (map-indexed #(assoc %2 :id (+ 2 %1)) (gen/sample (s/gen user-spec/user) 2)))
        _ (jdbc/insert-multi! (test-db) :comment [{:body "body1" :articleId 1 :userId 1}
                                                  {:body "body2" :articleId 1 :userId 2}
                                                  {:body "body3" :articleId 1 :userId 2}
                                                  {:body "body4" :articleId 1 :userId 3}])
        [ok? res] (core/article-comments nil "slug")]
    (is (true? ok?))
    (is (= 4 (-> res :comments count)))))

(deftest add-comment!--test
  (let [_       (jdbc/insert! (test-db) :article {:slug "slug"})
        inputs  (gen/sample (s/gen :core/add-comment) 20)
        results (map #(core/add-comment! auth-user "slug" %) inputs)]
    (is (every? true? (map first results)))
    (is (every? #(s/valid? :core/visible-comment (second %)) results))))

(deftest delete-comment!--comment-not-found--return-negative-response
  (let [[ok? res] (core/delete-comment! auth-user 1)]
    (is (false? ok?))
    (is (= {:errors {:id ["Cannot find a comment with given id."]}} res))))

(deftest delete-comment!--comment-is-not-owned-by-user--return-negative-response
  (let [_       (jdbc/insert! (test-db) :article {:slug "slug"})
        initial (gen/generate (s/gen :core/add-comment))
        [_ comment] (core/add-comment! auth-user "slug" initial)
        [ok? res] (core/delete-comment! (assoc auth-user :id 2)
                                        (-> comment :comment :id))]
    (is (false? ok?))
    (is (= {:errors {:authorization ["You need to be author of this comment to delete it."]}} res))))

(deftest delete-comment!--input-is-ok--delete-comment-and-return-positive-response
  (let [_              (jdbc/insert! (test-db) :article {:slug "slug"})
        initial-inputs (gen/sample (s/gen :core/add-comment) 20)
        create-res     (map #(core/add-comment! auth-user "slug" %) initial-inputs)
        update-res     (map #(core/delete-comment! auth-user (-> % second :comment :id)) create-res)]
    (is (every? #(= [true nil] %) update-res))))
