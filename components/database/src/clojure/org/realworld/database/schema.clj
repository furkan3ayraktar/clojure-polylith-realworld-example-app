(ns clojure.org.realworld.database.schema
  (:require [clojure.java.jdbc :refer :all]
            [environ.core :refer [env]]
            [clojure.org.realworld.database.core :as core]
            [taoensso.timbre :as log]))

(defn create-user-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :user
                                      [:id :integer :primary :key :autoincrement]
                                      [:email :text :unique]
                                      [:username :text :unique]
                                      [:password :text]
                                      [:image :text]
                                      [:bio :text]
                                      [:token :text :unique]))
    (catch Exception e
      (log/error e "An error occurred creating user table."))))

(defn create-user-follows-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :userFollows
                                      [:userId :integer "references user(id)"]
                                      [:followedUserId :integer "references user(id)"]))
    (catch Exception e
      (log/error e "An error occurred creating user-follows table."))))

(defn create-article-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :article
                                      [:id :integer :primary :key :autoincrement]
                                      [:slug :text :unique]
                                      [:title :text]
                                      [:description :text]
                                      [:body :text]
                                      [:createdAt :datetime]
                                      [:updatedAt :datetime]
                                      [:userId :integer "references user(id)"]))
    (catch Exception e
      (log/error e "An error occurred creating article table."))))

(defn create-tag-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :tag
                                      [:id :integer :primary :key :autoincrement]
                                      [:name :text :unique]))
    (catch Exception e
      (log/error e "An error occurred creating tag table."))))

(defn create-article-tags-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :articleTags
                                      [:articleId :integer "references article(id)"]
                                      [:tagId :integer "references tag(id)"]))
    (catch Exception e
      (log/error e "An error occurred creating article-tags table."))))

(defn create-favorite-articles-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :favoriteArticles
                                      [:articleId :integer "references article(id)"]
                                      [:userId :integer "references user(id)"]))
    (catch Exception e
      (log/error e "An error occurred creating favorite-articles table."))))

(defn create-comment-table []
  (try
    (db-do-commands (core/db)
                    (create-table-ddl :comment
                                      [:id :integer :primary :key :autoincrement]
                                      [:body :text]
                                      [:articleId :integer "references article(id)"]
                                      [:userId :integer "references user(id)"]
                                      [:createdAt :datetime]))
    (catch Exception e
      (log/error e "An error occurred creating comment table."))))

(defn generate-db []
  (create-user-table)
  (create-user-follows-table)
  (create-article-table)
  (create-tag-table)
  (create-article-tags-table)
  (create-favorite-articles-table)
  (create-comment-table))
