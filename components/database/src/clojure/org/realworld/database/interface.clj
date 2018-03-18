(ns clojure.org.realworld.database.interface
  (:require [clojure.org.realworld.database.core :as core]
            [clojure.org.realworld.database.schema :as schema]))

(defn db
  ([path]
   (core/db path))
  ([]
   (core/db)))

(defn generate-db [db]
  (schema/generate-db db))

(defn drop-db [db]
  (schema/drop-db db))
