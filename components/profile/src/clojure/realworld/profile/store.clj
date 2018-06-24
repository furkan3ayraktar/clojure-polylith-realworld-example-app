(ns clojure.realworld.profile.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.realworld.database.interface :as database]
            [honeysql.core :as sql]))

(defn following? [user-id followed-user-id]
  (let [query   {:select [:*]
                 :from   [:userFollows]
                 :where  [:and [:= :userId user-id]
                          [:= :followedUserId followed-user-id]]}
        results (jdbc/query (database/db) (sql/format query))]
    (-> results first nil? not)))

(defn follow! [user-id followed-user-id]
  (when-not (following? user-id followed-user-id)
    (jdbc/insert! (database/db) :userFollows {:userId         user-id
                                              :followedUserId followed-user-id})))

(defn unfollow! [user-id followed-user-id]
  (when (following? user-id followed-user-id)
    (let [query {:delete-from :userFollows
                 :where       [:and [:= :userId user-id]
                               [:= :followedUserId followed-user-id]]}]
      (jdbc/execute! (database/db) (sql/format query)))))
