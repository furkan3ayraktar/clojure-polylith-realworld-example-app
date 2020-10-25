(ns clojure.realworld.comment.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [honeysql.core :as sql]))

(defn comments [article-id]
  (let [query {:select [:*]
               :from   [:comment]
               :where  [:= :articleId article-id]}]
    (jdbc/query (database/db) (sql/format query) {:identifiers identity})))

(defn find-by-id [id]
  (let [query {:select [:*]
               :from   [:comment]
               :where  [:= :id id]}
        results (jdbc/query (database/db) (sql/format query) {:identifiers identity})]
    (first results)))

(defn add-comment! [comment-input]
  (let [result (jdbc/insert! (database/db) :comment
                             comment-input
                             {:entities identity})]
    (-> result first first val)))

(defn delete-comment! [id]
  (let [query {:delete-from :comment
               :where       [:= :id id]}]
    (jdbc/execute! (database/db) (sql/format query))
    nil))
