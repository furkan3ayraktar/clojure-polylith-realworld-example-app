(ns clojure.org.realworld.comment.store-test
  (:require [clj-time.core :as t]
            [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.comment.store :as store]
            [clojure.org.realworld.database.interface :as database]
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

(deftest comments--no-comments--return-empty-vector
  (let [res (store/comments 1)]
    (is (= [] res))))

(deftest comments--some-comments--return-all-comments
  (let [_   (jdbc/insert-multi! (test-db) :comment [{:articleId 1 :body "body1"}
                                                    {:articleId 1 :body "body2"}
                                                    {:articleId 1 :body "body3"}
                                                    {:articleId 1 :body "body4"}])
        res (store/comments 1)]
    (is (= 4 (count res)))))

(deftest find-by-id--comment-exists--return-comment
  (let [_       (jdbc/insert! (test-db) :comment {:body "body"})
        comment (store/find-by-id 1)]
    (is (= "body" (:body comment)))))

(deftest find-by-id--comment-does-not-exist--return-nil
  (let [comment (store/find-by-id 1)]
    (is (nil? comment))))

(deftest add-comment!--test
  (let [now     (t/now)
        comment {:body      "body"
                 :createdAt now
                 :updatedAt now
                 :userId    1
                 :articleId 1}
        res     (store/add-comment! comment)
        added   (store/find-by-id 1)]
    (is (= (assoc comment :id 1
                          :createdAt (-> comment :createdAt str)
                          :updatedAt (-> comment :updatedAt str))
           added))
    (is (= 1 res))))

(deftest delete-comment!--test
  (let [now            (t/now)
        _              (store/add-comment! {:body      "body"
                                            :createdAt now
                                            :updatedAt now
                                            :userId    1
                                            :articleId 1})
        comment-before (store/find-by-id 1)
        _              (store/delete-comment! 1)
        comment-after  (store/find-by-id 1)]
    (is (not (nil? comment-before)))
    (is (nil? comment-after))))
