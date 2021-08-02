(ns clojure.realworld.user.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [clojure.realworld.user.spec :as spec]
            [clojure.spec.alpha :as s]
            [honeysql.core :as sql]))

(defn find-by [key value]
  (let [query {:select [:*]
               :from   [:user]
               :where  [:= key value]}
        results (jdbc/query (database/db) (sql/format query))]
    (first results)))

(defn find-by-email [email]
  (find-by :email email))

(defn find-by-username [username]
  (find-by :username username))

(defn find-by-id [id]
  (find-by :id id))

(defn find-by-username-or-id [username-or-id]
  (if (s/valid? spec/id username-or-id)
    (find-by-id username-or-id)
    (find-by-username username-or-id)))

(defn insert-user! [user-input]
  (jdbc/insert! (database/db) :user user-input))

(defn update-user! [id user-input]
  (let [query {:update :user
               :set    user-input
               :where  [:= :id id]}]
    (jdbc/execute! (database/db) (sql/format query))))
