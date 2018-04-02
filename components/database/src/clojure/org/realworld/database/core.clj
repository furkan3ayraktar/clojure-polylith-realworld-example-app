(ns clojure.org.realworld.database.core
  (:require [environ.core :refer [env]]))

(defn- db-path []
  (if (contains? env :database)
    (env :database)
    "database.db"))

(defn db
  ([path]
   {:classname   "org.sqlite.JDBC"
    :subprotocol "sqlite"
    :subname     path})
  ([]
   (db (db-path))))
