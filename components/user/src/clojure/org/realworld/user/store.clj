(ns clojure.org.realworld.user.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [clojure.spec.alpha :as s]
            [java-jdbc.sql :as sql]))

(defn find-by [key value]
  (let [results (jdbc/query (database/db)
                            (sql/select * :user (sql/where {key value})))]
    (first results)))

(defn find-by-email [email]
  (find-by :email email))

(defn find-by-username [username]
  (find-by :username username))

(defn find-by-id [id]
  (find-by :id id))

(defn find-by-username-or-id [username-or-id]
  (if (s/valid? :user/id username-or-id)
    (find-by-id username-or-id)
    (find-by-username username-or-id)))

(defn find-by-token [token]
  (find-by :token token))

(defn update-token! [email new-token]
  (jdbc/update! (database/db)
                :user
                {:token new-token}
                (sql/where {:email email})))

(defn insert-user! [user-input]
  (jdbc/insert! (database/db) :user user-input))

(defn update-user! [id user-input]
  (jdbc/update! (database/db)
                :user
                user-input
                (sql/where {:id id})))
