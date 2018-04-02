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
        _     (jdbc/insert! (test-db) :article input)
        [ok? res] (core/article nil "this-is-slug")]
    (is (true? ok?))
    (is (= 1 (-> res :article :id)))
    (is (= "title" (-> res :article :title)))
    (is (= "this-is-slug" (-> res :article :slug)))))

(deftest create-article--test
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        inputs    (gen/sample (s/gen :core/create-article) 20)
        results   (map #(core/create-article! auth-user %) inputs)]
    (is (every? true? (map first results)))
    (is (every? #(s/valid? :core/visible-article (second %)) results))))

(deftest update-article--article-not-found--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        input     (gen/generate (s/gen :core/update-article))
        [ok? res] (core/update-article! auth-user "slug" input)]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest update-article--article-is-not-owned-by-user--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        initial   (gen/generate (s/gen :core/create-article))
        [_ article] (core/create-article! auth-user initial)
        input     (gen/generate (s/gen :core/update-article))
        [ok? res] (core/update-article! (assoc auth-user :id 2)
                                        (-> article :article :slug)
                                        input)]
    (is (false? ok?))
    (is (= {:errors {:authorization ["You need to be author of this article to update it."]}} res))))

(deftest update-article--input-is-ok--update-article-and-return-positive-response
  (let [auth-user      (assoc (gen/generate (s/gen :core/user)) :id 1)
        _              (jdbc/insert! (test-db) :user auth-user)
        initial-inputs (gen/sample (s/gen :core/create-article) 20)
        create-res     (map #(core/create-article! auth-user %) initial-inputs)
        inputs         (gen/sample (s/gen :core/update-article) 20)
        update-res     (map-indexed #(core/update-article! auth-user
                                                           (-> (nth create-res %1) second :article :slug)
                                                           %2)
                                    inputs)]
    (is (every? true? (map first update-res)))
    (is (every? #(s/valid? :core/visible-article (second %)) update-res))))

(deftest delete-article--article-not-found--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [ok? res] (core/delete-article! auth-user "slug")]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest delete-article--article-is-not-owned-by-user--return-negative-response
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        initial   (gen/generate (s/gen :core/create-article))
        [_ article] (core/create-article! auth-user initial)
        [ok? res] (core/delete-article! (assoc auth-user :id 2)
                                        (-> article :article :slug))]
    (is (false? ok?))
    (is (= {:errors {:authorization ["You need to be author of this article to delete it."]}} res))))

(deftest delete-article--input-is-ok--delete-article-and-return-positive-response
  (let [auth-user      (assoc (gen/generate (s/gen :core/user)) :id 1)
        _              (jdbc/insert! (test-db) :user auth-user)
        initial-inputs (gen/sample (s/gen :core/create-article) 20)
        create-res     (map #(core/create-article! auth-user %) initial-inputs)
        update-res     (map #(core/delete-article! auth-user (-> % second :article :slug)) create-res)]
    (is (every? #(= [true nil] %) update-res))))

(deftest favorite-article!--profile-not-found--return-negative-result
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [ok? res] (core/favorite-article! auth-user "slug")]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest favorite-article!--profile-found--return-positive-result
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [_ article] (core/create-article! auth-user (gen/generate (s/gen :core/create-article)))
        [ok? res] (core/favorite-article! auth-user (-> article :article :slug))]
    (is (true? ok?))
    (is (= 1 (-> res :article :favoritesCount)))
    (is (true? (-> res :article :favorited)))))

(deftest unfavorite-article!--profile-not-found--return-negative-result
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [ok? res] (core/unfavorite-article! auth-user "slug")]
    (is (false? ok?))
    (is (= {:errors {:slug ["Cannot find an article with given slug."]}} res))))

(deftest unfavorite-article!--logged-in-and-profile-found--return-positive-result
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [_ article] (core/create-article! auth-user (gen/generate (s/gen :core/create-article)))
        _         (core/favorite-article! auth-user (-> article :article :slug))
        [ok? res] (core/unfavorite-article! auth-user (-> article :article :slug))]
    (is (true? ok?))
    (is (= 0 (-> res :article :favoritesCount)))
    (is (false? (-> res :article :favorited)))))

(deftest feed--no-articles-found--return-response-with-empty-vector
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [ok? res] (core/feed auth-user 10 0)]
    (is (true? ok?))
    (is (= {:articles      []
            :articlesCount 0}
           res))))

(deftest feed--articles-found--return-response
  (let [auth-user  (assoc (gen/generate (s/gen :core/user)) :id 1)
        other-user (assoc (gen/generate (s/gen :core/user)) :id 2)
        _          (jdbc/insert-multi! (test-db) :user [auth-user other-user])
        _          (jdbc/insert! (test-db) :userFollows {:userId 1 :followedUserId 2})
        articles   (gen/sample (s/gen :core/create-article) 20)
        _          (doseq [a articles]
                     (core/create-article! other-user a))
        [ok? res] (core/feed auth-user 10 0)]
    (is (true? ok?))
    (is (= 10 (:articlesCount res)))
    (is (= 10 (-> res :articles count)))
    (is (= (map :title (take 10 (reverse articles)))
           (map :title (:articles res))))))

(deftest feed--multiple-followed-users--return-response
  (let [auth-user    (assoc (gen/generate (s/gen :core/user)) :id 1)
        other-user   (assoc (gen/generate (s/gen :core/user)) :id 2)
        other-user-2 (assoc (gen/generate (s/gen :core/user)) :id 3)
        _            (jdbc/insert-multi! (test-db) :user [auth-user other-user])
        _            (jdbc/insert-multi! (test-db) :userFollows [{:userId 1 :followedUserId 2}
                                                                 {:userId 1 :followedUserId 3}])
        articles     (gen/sample (s/gen :core/create-article) 20)
        _            (doseq [a (take 10 articles)]
                       (core/create-article! other-user a))
        _            (doseq [a (take 10 (drop 10 articles))]
                       (core/create-article! other-user-2 a))
        [ok? res] (core/feed auth-user 10 0)]
    (is (true? ok?))
    (is (= 10 (:articlesCount res)))
    (is (= 10 (-> res :articles count)))
    (is (= (map :title (take 10 (reverse articles)))
           (map :title (:articles res))))))

(deftest feed--no-limit-provided--return-response-with-limit-20
  (let [auth-user  (assoc (gen/generate (s/gen :core/user)) :id 1)
        other-user (assoc (gen/generate (s/gen :core/user)) :id 2)
        _          (jdbc/insert-multi! (test-db) :user [auth-user other-user])
        _          (jdbc/insert! (test-db) :userFollows {:userId 1 :followedUserId 2})
        articles   (gen/sample (s/gen :core/create-article) 20)
        _          (doseq [a articles]
                     (core/create-article! other-user a))
        [ok? res] (core/feed auth-user nil nil)]
    (is (true? ok?))
    (is (= 20 (:articlesCount res)))
    (is (= 20 (-> res :articles count)))
    (is (= (map :title (take 20 (reverse articles)))
           (map :title (:articles res))))))

(deftest feed--offset-provided--return-response-with-limit-20
  (let [auth-user  (assoc (gen/generate (s/gen :core/user)) :id 1)
        other-user (assoc (gen/generate (s/gen :core/user)) :id 2)
        _          (jdbc/insert-multi! (test-db) :user [auth-user other-user])
        _          (jdbc/insert! (test-db) :userFollows {:userId 1 :followedUserId 2})
        articles   (gen/sample (s/gen :core/create-article) 20)
        _          (doseq [a articles]
                     (core/create-article! other-user a))
        [ok? res] (core/feed auth-user nil 5)]
    (is (true? ok?))
    (is (= 15 (:articlesCount res)))
    (is (= 15 (-> res :articles count)))
    (is (= (map :title (take 15 (drop 5 (reverse articles))))
           (map :title (:articles res))))))

(deftest articles--no-articles-found--return-response-with-empty-vector
  (let [auth-user (assoc (gen/generate (s/gen :core/user)) :id 1)
        _         (jdbc/insert! (test-db) :user auth-user)
        [ok? res] (core/articles auth-user 10 0 nil nil nil)]
    (is (true? ok?))
    (is (= {:articles      []
            :articlesCount 0}
           res))))

(deftest articles--articles-found--return-response
  (let [auth-user  (assoc (gen/generate (s/gen :core/user)) :id 1)
        other-user (assoc (gen/generate (s/gen :core/user)) :id 2)
        _          (jdbc/insert-multi! (test-db) :user [auth-user other-user])
        articles   (gen/sample (s/gen :core/create-article) 20)
        _          (doseq [a articles]
                     (core/create-article! other-user a))
        [ok? res] (core/articles auth-user 10 0 nil nil nil)]
    (is (true? ok?))
    (is (= 10 (:articlesCount res)))
    (is (= 10 (-> res :articles count)))
    (is (= (map :title (take 10 (reverse articles)))
           (map :title (:articles res))))))
