(ns clojure.org.realworld.database.interface
  (:require [clojure.org.realworld.database.core :as core]
            [clojure.org.realworld.database.schema :as schema]))

(defn db []
  (core/db))

(defn generate-db []
  (schema/generate-db))
