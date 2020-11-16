(ns clojure.realworld.database.core
  (:require [clojure.java.io :as io]
            [clojure.realworld.env.interface :as env]))

(defn- db-path []
  (if (contains? env/env :database)
    (env/env :database)
    "database.db"))

(defn db
  ([path]
   {:classname   "org.sqlite.JDBC"
    :subprotocol "sqlite"
    :subname     path})
  ([]
   (db (db-path))))

(defn db-exists? []
  (let [db-file (io/file "database.db")]
    (.exists db-file)))
