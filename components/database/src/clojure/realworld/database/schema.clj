(ns clojure.realworld.database.schema
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.log.interface :as log]
            [honeysql.core :as sql]))

(def user
  (jdbc/create-table-ddl :user
                         [[:id :integer :primary :key :autoincrement]
                          [:email :text :unique]
                          [:username :text :unique]
                          [:password :text]
                          [:image :text]
                          [:bio :text]
                          [:token :text :unique]]
                         {:entities identity}))

(def user-follows
  (jdbc/create-table-ddl :userFollows
                         [[:userId :integer "references user(id)"]
                          [:followedUserId :integer "references user(id)"]]
                         {:entities identity}))

(def article
  (jdbc/create-table-ddl :article
                         [[:id :integer :primary :key :autoincrement]
                          [:slug :text :unique]
                          [:title :text]
                          [:description :text]
                          [:body :text]
                          [:createdAt :datetime]
                          [:updatedAt :datetime]
                          [:userId :integer "references user(id)"]]
                         {:entities identity}))

(def tag
  (jdbc/create-table-ddl :tag
                         [[:id :integer :primary :key :autoincrement]
                          [:name :text :unique]]
                         {:entities identity}))

(def article-tags
  (jdbc/create-table-ddl :articleTags
                         [[:articleId :integer "references article(id)"]
                          [:tagId :integer "references tag(id)"]]
                         {:entities identity}))

(def favorite-articles
  (jdbc/create-table-ddl :favoriteArticles
                         [[:articleId :integer "references article(id)"]
                          [:userId :integer "references user(id)"]]
                         {:entities identity}))

(def comment-table
  (jdbc/create-table-ddl :comment
                         [[:id :integer :primary :key :autoincrement]
                          [:body :text]
                          [:articleId :integer "references article(id)"]
                          [:userId :integer "references user(id)"]
                          [:createdAt :datetime]
                          [:updatedAt :datetime]]
                         {:entities identity}))

(defn generate-db [db]
  (jdbc/db-do-commands db
                       [user
                        user-follows
                        article
                        tag
                        article-tags
                        favorite-articles
                        comment-table]))

(defn drop-db [db]
  (jdbc/db-do-commands db
                       [(jdbc/drop-table-ddl :user)
                        (jdbc/drop-table-ddl :userFollows)
                        (jdbc/drop-table-ddl :article)
                        (jdbc/drop-table-ddl :tag)
                        (jdbc/drop-table-ddl :articleTags)
                        (jdbc/drop-table-ddl :favoriteArticles)
                        (jdbc/drop-table-ddl :comment)]))

(defn table->schema-item [{:keys [tbl_name sql]}]
  [(keyword tbl_name) sql])

(defn valid-schema? [db]
  (let [query          {:select [:*]
                        :from   [:sqlite_master]
                        :where  [:= :type "table"]}
        tables         (jdbc/query db (sql/format query) {:identifiers identity})
        current-schema (select-keys (into {} (map table->schema-item tables))
                                    [:user :userFollows :article :tag :articleTags :favoriteArticles :comment])
        valid-schema   {:user             user
                        :userFollows      user-follows
                        :article          article
                        :tag              tag
                        :articleTags      article-tags
                        :favoriteArticles favorite-articles
                        :comment          comment-table}]
    (if (= valid-schema current-schema)
      true
      (do
        (log/warn "Current schema is invalid. Please correct it and restart the server.")
        false))))

