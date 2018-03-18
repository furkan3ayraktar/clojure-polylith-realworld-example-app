(ns clojure.org.realworld.profile.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [java-jdbc.sql :as sql]))

(defn following? [user-id followed-user-id]
  (let [results (jdbc/query (database/db)
                            (sql/select *
                                        :userFollows
                                        (sql/where {:userId user-id
                                                    :followedUserId followed-user-id})))]
    (-> results first nil? not)))

(defn follow! [user-id followed-user-id]
  (when-not (following? user-id followed-user-id)
    (jdbc/insert! (database/db) :userFollows {:userId user-id
                                              :followedUserId followed-user-id})))

(defn unfollow! [user-id followed-user-id]
  (when (following? user-id followed-user-id)
    (jdbc/delete! (database/db) :userFollows (sql/where
                                               {:userId user-id
                                                :followedUserId followed-user-id}))))
