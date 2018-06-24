(ns clojure.realworld.article.store-test
  (:require [clj-time.core :as t]
            [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [clojure.realworld.article.store :as store]
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

(deftest find-by-slug--test
  (let [_       (jdbc/insert! (test-db) :article {:slug "this-is-slug"})
        res1    (store/find-by-slug "this-is-slug")
        article {:body        nil
                 :createdAt   nil
                 :description nil
                 :id          1
                 :slug        "this-is-slug"
                 :title       nil
                 :updatedAt   nil
                 :userId      nil}
        res2    (store/find-by-slug "wrong-slug")]
    (is (= article res1))
    (is (nil? res2))))

(deftest insert-article!--test
  (let [now     (t/now)
        article {:slug        "slug"
                 :title       "title"
                 :description "description"
                 :body        "body"
                 :createdAt   now
                 :updatedAt   now
                 :userId      1}
        _       (store/insert-article! article)
        res     (store/find-by-slug "slug")]
    (is (= (assoc article :id 1
                          :createdAt (-> article :createdAt str)
                          :updatedAt (-> article :updatedAt str))
           res))))

(deftest tags-with-names--test
  (let [_   (jdbc/insert-multi! (test-db) :tag [{:name "tag1"}
                                                {:name "tag2"}
                                                {:name "tag3"}
                                                {:name "tag4"}
                                                {:name "tag5"}
                                                {:name "tag6"}])
        res (store/tags-with-names ["tag1" "tag2" "tag3"])]
    (is (= [{:id   1
             :name "tag1"}
            {:id   2
             :name "tag2"}
            {:id   3
             :name "tag3"}
            res]))))

(deftest add-tags-to-article!--test
  (let [_   (jdbc/insert-multi! (test-db) :tag [{:name "tag1"}
                                                {:name "tag2"}
                                                {:name "tag3"}
                                                {:name "tag4"}
                                                {:name "tag5"}
                                                {:name "tag6"}])
        _   (store/add-tags-to-article! 1 ["tag1" "tag2" "tag3"])
        res (store/article-tags 1)]
    (is (= ["tag1" "tag2" "tag3"] res))))

(deftest article-tags--test
  (let [_   (jdbc/insert-multi! (test-db) :tag [{:name "tag1"}
                                                {:name "tag2"}
                                                {:name "tag3"}
                                                {:name "tag4"}
                                                {:name "tag5"}
                                                {:name "tag6"}])
        _   (jdbc/insert-multi! (test-db) :articleTags [{:articleId 1 :tagId 1}
                                                        {:articleId 1 :tagId 2}
                                                        {:articleId 1 :tagId 3}
                                                        {:articleId 2 :tagId 3}
                                                        {:articleId 2 :tagId 4}
                                                        {:articleId 2 :tagId 5}])
        res (store/article-tags 1)]
    (is (= ["tag1" "tag2" "tag3"] res))))

(deftest insert-tag!--tag-exists--do-nothing
  (let [_   (jdbc/insert! (test-db) :tag {:name "tag1"})
        res (store/insert-tag! "tag1")]
    (is (nil? res))))

(deftest insert-tag!--tag-does-not-exist--insert-tag
  (let [_    (store/insert-tag! "tag1")
        tags (store/tags-with-names ["tag1"])]
    (is (= [{:id   1
             :name "tag1"}]
           tags))))

(deftest favorited?--not-favorited--return-false
  (let [favorited? (store/favorited? 1 1)]
    (is (false? favorited?))))

(deftest favorited?--favorited--return-true
  (let [_          (jdbc/insert! (test-db) :favoriteArticles {:articleId 1 :userId 1})
        favorited? (store/favorited? 1 1)]
    (is (true? favorited?))))

(deftest favorites-count--not-favorited--return-0
  (let [favorites-count (store/favorites-count 1)]
    (is (= 0 favorites-count))))

(deftest favorites-count--favorited--return-favorites-count
  (let [_               (jdbc/insert-multi! (test-db) :favoriteArticles [{:articleId 1 :userId 1}
                                                                         {:articleId 1 :userId 2}
                                                                         {:articleId 1 :userId 3}])
        favorites-count (store/favorites-count 1)]
    (is (= 3 favorites-count))))

(deftest update-article!--test
  (let [now         (t/now)
        _           (store/insert-article! {:slug        "slug"
                                            :title       "title"
                                            :description "description"
                                            :body        "body"
                                            :createdAt   now
                                            :updatedAt   now
                                            :userId      1})
        update-time (t/now)
        article     {:slug        "updated-slug"
                     :title       "updated-title"
                     :description "description"
                     :body        "body"
                     :updatedAt   update-time}
        _           (store/update-article! 1 article)
        res         (store/find-by-slug "updated-slug")]
    (is (= (assoc article :id 1
                          :createdAt (str now)
                          :updatedAt (str update-time)
                          :userId 1)
           res))))

(deftest delete-article!--test
  (let [now                    (t/now)
        _                      (store/insert-article! {:slug        "slug"
                                                       :title       "title"
                                                       :description "description"
                                                       :body        "body"
                                                       :createdAt   now
                                                       :updatedAt   now
                                                       :userId      1})
        _                      (jdbc/insert-multi! (test-db) :tag [{:name "tag1"} {:name "tag2"} {:name "tag3"}])
        _                      (jdbc/insert-multi! (test-db) :articleTags [{:tagId 1 :articleId 1}
                                                                           {:tagId 2 :articleId 1}
                                                                           {:tagId 3 :articleId 1}])
        _                      (jdbc/insert-multi! (test-db) :favoriteArticles [{:userId 1 :articleId 1}
                                                                                {:userId 2 :articleId 1}
                                                                                {:userId 3 :articleId 1}])
        article-before         (store/find-by-slug "slug")
        favorites-count-before (store/favorites-count 1)
        tags-before            (store/article-tags 1)
        _                      (store/delete-article! 1)
        article-after          (store/find-by-slug "slug")
        favorites-count-after  (store/favorites-count 1)
        tags-after             (store/article-tags 1)]
    (is (not (nil? article-before)))
    (is (= 3 favorites-count-before))
    (is (= ["tag1" "tag2" "tag3"] tags-before))
    (is (nil? article-after))
    (is (= 0 favorites-count-after))
    (is (= [] tags-after))))

(deftest favorite!--currently-not-favorited--insert-user-favorite
  (let [before-favorited? (store/favorited? 1 2)
        _                 (store/favorite! 1 2)
        after-favorited?  (store/favorited? 1 2)]
    (is (false? before-favorited?))
    (is (true? after-favorited?))))

(deftest favorite!--currently-favorited--do-nothing
  (let [_                 (store/favorite! 1 2)
        before-favorited? (store/favorited? 1 2)
        _                 (store/favorite! 1 2)
        after-favorited?  (store/favorited? 1 2)]
    (is (true? before-favorited?))
    (is (true? after-favorited?))))

(deftest unfavorite!--currently-favorited--delete-user-favorite
  (let [_                 (store/favorite! 1 2)
        before-favorited? (store/favorited? 1 2)
        _                 (store/unfavorite! 1 2)
        after-favorited?  (store/favorited? 1 2)]
    (is (true? before-favorited?))
    (is (false? after-favorited?))))

(deftest unfavorite!--currently-not-favorited--do-nothing
  (let [before-favorited? (store/favorited? 1 2)
        _                 (store/unfavorite! 1 2)
        after-favorited?  (store/favorited? 1 2)]
    (is (false? before-favorited?))
    (is (false? after-favorited?))))

(deftest feed--no-articles-found--return-empty-vector
  (let [res (store/feed 1 10 0)]
    (is (= [] res))))

(deftest feed--some-articles-found--return-articles
  (let [_   (jdbc/insert-multi! (test-db) :article [{:slug "slug1" :userId 1}
                                                    {:slug "slug2" :userId 1}
                                                    {:slug "slug3" :userId 1}
                                                    {:slug "slug4" :userId 2}
                                                    {:slug "slug5" :userId 2}
                                                    {:slug "slug6" :userId 3}])
        _   (jdbc/insert-multi! (test-db) :userFollows [{:userId 4 :followedUserId 1}
                                                        {:userId 4 :followedUserId 3}])
        res (store/feed 4 10 0)]
    (is (= ["slug6" "slug3" "slug2" "slug1"]
           (mapv :slug res)))))

(deftest feed--more-than-10-articles-found--return-10-articles
  (let [articles (mapv #(hash-map :slug (str "slug" %) :userId 1) (range 30))
        _        (jdbc/insert-multi! (test-db) :article articles)
        _        (jdbc/insert-multi! (test-db) :userFollows [{:userId 2 :followedUserId 1}])
        res1     (store/feed 2 10 0)
        res2     (store/feed 2 10 10)]
    (is (= (take 10 (reverse articles))
           (mapv #(select-keys % [:slug :userId]) res1)))
    (is (= (take 10 (drop 10 (reverse articles)))
           (mapv #(select-keys % [:slug :userId]) res2)))))

(deftest articles-by-tag--some-articles-found--return-articles
  (let [_    (jdbc/insert-multi! (test-db) :article [{:slug "slug1"}
                                                     {:slug "slug2"}
                                                     {:slug "slug3"}
                                                     {:slug "slug4"}
                                                     {:slug "slug5"}
                                                     {:slug "slug6"}])
        _    (jdbc/insert-multi! (test-db) :tag [{:name "tag1"}
                                                 {:name "tag2"}])
        _    (jdbc/insert-multi! (test-db) :articleTags [{:articleId 1 :tagId 1}
                                                         {:articleId 1 :tagId 2}
                                                         {:articleId 2 :tagId 2}
                                                         {:articleId 3 :tagId 2}
                                                         {:articleId 4 :tagId 1}
                                                         {:articleId 6 :tagId 1}])
        res1 (store/articles-by-tag 10 0 "tag1")
        res2 (store/articles-by-tag 10 0 "tag2")]
    (is (= ["slug6" "slug4" "slug1"]
           (mapv :slug res1)))
    (is (= ["slug3" "slug2" "slug1"]
           (mapv :slug res2)))))

(deftest articles-by-author--some-articles-found--return-articles
  (let [_    (jdbc/insert-multi! (test-db) :user [{:username "username1"}
                                                  {:username "username2"}])
        _    (jdbc/insert-multi! (test-db) :article [{:slug "slug1" :userId 1}
                                                     {:slug "slug2" :userId 1}
                                                     {:slug "slug3" :userId 2}
                                                     {:slug "slug4" :userId 2}
                                                     {:slug "slug5" :userId 1}
                                                     {:slug "slug6" :userId 2}])
        res1 (store/articles-by-author 10 0 "username1")
        res2 (store/articles-by-author 10 0 "username2")
        res3 (store/articles-by-author 10 0 "username3")]
    (is (= ["slug5" "slug2" "slug1"]
           (mapv :slug res1)))
    (is (= ["slug6" "slug4" "slug3"]
           (mapv :slug res2)))
    (is (empty? res3))))

(deftest articles-by-author--some-articles-found--return-articles
  (let [_    (jdbc/insert-multi! (test-db) :user [{:username "username1"}
                                                  {:username "username2"}])
        _    (jdbc/insert-multi! (test-db) :article [{:slug "slug1" :userId 1}
                                                     {:slug "slug2" :userId 1}
                                                     {:slug "slug3" :userId 2}
                                                     {:slug "slug4" :userId 2}
                                                     {:slug "slug5" :userId 1}
                                                     {:slug "slug6" :userId 2}])
        _    (jdbc/insert-multi! (test-db) :favoriteArticles [{:articleId 1 :userId 1}
                                                              {:articleId 2 :userId 1}
                                                              {:articleId 3 :userId 2}
                                                              {:articleId 6 :userId 1}])
        res1 (store/articles-by-favorited 10 0 "username1")
        res2 (store/articles-by-favorited 10 0 "username2")
        res3 (store/articles-by-favorited 10 0 "username3")]
    (is (= ["slug6" "slug2" "slug1"]
           (mapv :slug res1)))
    (is (= ["slug3"]
           (mapv :slug res2)))
    (is (empty? res3))))

(deftest articles--no-other-filters--return-articles
  (let [_   (jdbc/insert-multi! (test-db) :article [{:slug "slug1"}
                                                    {:slug "slug2"}
                                                    {:slug "slug3"}
                                                    {:slug "slug4"}
                                                    {:slug "slug5"}
                                                    {:slug "slug6"}])
        res (store/articles 10 0 nil nil nil)]
    (is (= ["slug6" "slug5" "slug4" "slug3" "slug2" "slug1"]
           (mapv :slug res)))))
