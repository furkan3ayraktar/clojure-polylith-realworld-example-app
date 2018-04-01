(ns clojure.org.realworld.comments.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [java-jdbc.sql :as sql]))

(defn comments [article-id]
  (jdbc/query (database/db)
              (sql/select * :comment (sql/where {:articleId article-id}))
              {:identifiers identity}))

(defn find-by-id [id]
  (let [results (jdbc/query (database/db)
                            (sql/select * :comment (sql/where {:id id}))
                            {:identifiers identity})]
    (first results)))

(defn add-comment! [comment-input]
  (let [result (jdbc/insert! (database/db) :comment
                             comment-input
                             {:entities identity})]
    (-> result first first val)))

(defn delete-comment! [id]
  (jdbc/delete! (database/db) :comment (sql/where {:id id}))
  nil)
