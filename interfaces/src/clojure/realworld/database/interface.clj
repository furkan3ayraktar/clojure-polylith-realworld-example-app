(ns clojure.realworld.database.interface)

(defn db
  ([path])
  ([]))

(defn db-exists? [])

(defn generate-db [db])

(defn drop-db [db])

(defn valid-schema? [db])
