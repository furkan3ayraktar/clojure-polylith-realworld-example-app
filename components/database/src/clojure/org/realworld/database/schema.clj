(ns clojure.org.realworld.database.schema
  (:require [clojure.java.jdbc :refer :all]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]))

(defn create-user-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :user
                                      [[:id :integer :primary :key :autoincrement]
                                       [:email :text :unique]
                                       [:username :text :unique]
                                       [:password :text]
                                       [:image :text]
                                       [:bio :text]
                                       [:token :text :unique]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating user table."))))

(defn create-user-follows-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :userFollows
                                      [[:userId :integer "references user(id)"]
                                       [:followedUserId :integer "references user(id)"]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating user-follows table."))))

(defn create-article-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :article
                                      [[:id :integer :primary :key :autoincrement]
                                       [:slug :text :unique]
                                       [:title :text]
                                       [:description :text]
                                       [:body :text]
                                       [:createdAt :datetime]
                                       [:updatedAt :datetime]
                                       [:userId :integer "references user(id)"]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating article table."))))

(defn create-tag-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :tag
                                      [[:id :integer :primary :key :autoincrement]
                                       [:name :text :unique]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating tag table."))))

(defn create-article-tags-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :articleTags
                                      [[:articleId :integer "references article(id)"]
                                       [:tagId :integer "references tag(id)"]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating article-tags table."))))

(defn create-favorite-articles-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :favoriteArticles
                                      [[:articleId :integer "references article(id)"]
                                       [:userId :integer "references user(id)"]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating favorite-articles table."))))

(defn create-comment-table [db]
  (try
    (db-do-commands db
                    (create-table-ddl :comment
                                      [[:id :integer :primary :key :autoincrement]
                                       [:body :text]
                                       [:articleId :integer "references article(id)"]
                                       [:userId :integer "references user(id)"]
                                       [:createdAt :datetime]
                                       [:updatedAt :datetime]]
                                      {:entities identity}))
    (catch Exception e
      (log/error e "An error occurred creating comment table."))))

(defn generate-db [db]
  (create-user-table db)
  (create-user-follows-table db)
  (create-article-table db)
  (create-tag-table db)
  (create-article-tags-table db)
  (create-favorite-articles-table db)
  (create-comment-table db))

(defn drop-db [db]
  (db-do-commands db
                  [(drop-table-ddl :user)
                   (drop-table-ddl :userFollows)
                   (drop-table-ddl :article)
                   (drop-table-ddl :tag)
                   (drop-table-ddl :articleTags)
                   (drop-table-ddl :favoriteArticles)
                   (drop-table-ddl :comment)]))
