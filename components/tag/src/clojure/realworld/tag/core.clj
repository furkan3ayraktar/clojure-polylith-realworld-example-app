(ns clojure.realworld.tag.core
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [honeysql.core :as sql]))

(defn all-tags []
  (let [query {:select [:name]
               :from   [:tag]}
        result (jdbc/query (database/db) (sql/format query))
        res {:tags (mapv :name result)}]
    [true res]))
