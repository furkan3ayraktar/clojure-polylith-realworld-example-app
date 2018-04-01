(ns clojure.org.realworld.tags.core
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [java-jdbc.sql :as sql]))

(defn all-tags []
  (let [result (jdbc/query (database/db) (sql/select [:name] :tag))
        res    {:tags (mapv :name result)}]
    [true res]))
