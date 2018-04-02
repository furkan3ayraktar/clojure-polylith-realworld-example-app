(ns clojure.org.realworld.article.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [honeysql.core :as sql]))

(defn find-by-slug [slug]
  (let [query   {:select [:*]
                 :from   [:article]
                 :where  [:= :slug slug]}
        results (jdbc/query (database/db) (sql/format query) {:identifiers identity})]
    (first results)))

(defn insert-article! [article-input]
  (jdbc/insert! (database/db) :article article-input {:entities identity}))

(defn tags-with-names [tag-names]
  (let [query   {:select [:*]
                 :from   [:tag]
                 :where  [:in :name tag-names]}
        results (jdbc/query (database/db) (sql/format query))]
    results))

(defn add-tags-to-article! [article-id tag-names]
  (when-not (empty? tag-names)
    (let [tags   (tags-with-names tag-names)
          inputs (mapv #(hash-map :articleId article-id
                                  :tagId (:id %))
                       tags)]
      (jdbc/insert-multi! (database/db) :articleTags inputs))))

(defn article-tags [article-id]
  (let [query   {:select [:name]
                 :from   [:articleTags]
                 :join   [:tag [:= :tagId :tag.id]]
                 :where  [:= :articleId article-id]}
        results (jdbc/query (database/db) (sql/format query))]
    (mapv :name results)))

(defn insert-tag! [name]
  (let [tag (first (tags-with-names [name]))]
    (when-not tag
      (jdbc/insert! (database/db) :tag {:name name}))))

(defn update-tags! [tag-names]
  (when-not (empty? tag-names)
    (doseq [name tag-names]
      (insert-tag! name))))

(defn favorited? [user-id article-id]
  (let [query  {:select [:%count.*]
                :from   [:favoriteArticles]
                :where  [:and [:= :articleId article-id]
                         [:= :userId user-id]]}
        result (jdbc/query (database/db) (sql/format query))]
    (< 0 (-> result first first val))))

(defn favorites-count [article-id]
  (let [query  {:select [:%count.*]
                :from   [:favoriteArticles]
                :where  [:= :articleId article-id]}
        result (jdbc/query (database/db) (sql/format query))]
    (-> result first first val)))

(defn update-article! [id article-input]
  (let [query {:update :article
               :set    article-input
               :where  [:= :id id]}]
    (jdbc/execute! (database/db) (sql/format query))))

(defn delete-article! [id]
  (let [query-1 {:delete-from :articleTags
                 :where       [:= :articleId id]}
        query-2 {:delete-from :favoriteArticles
                 :where       [:= :articleId id]}
        query-3 {:delete-from :article
                 :where       [:= :id id]}]
    (jdbc/with-db-transaction [trans-conn (database/db)]
      (jdbc/execute! trans-conn (sql/format query-1))
      (jdbc/execute! trans-conn (sql/format query-2))
      (jdbc/execute! trans-conn (sql/format query-3)))
    nil))

(defn favorite! [user-id article-id]
  (when-not (favorited? user-id article-id)
    (jdbc/insert! (database/db) :favoriteArticles {:articleId article-id
                                                   :userId    user-id})))

(defn unfavorite! [user-id article-id]
  (when (favorited? user-id article-id)
    (let [query {:delete-from :favoriteArticles
                 :where       [:and [:= :articleId article-id]
                                    [:= :userId user-id]]}]
      (jdbc/execute! (database/db) (sql/format query)))))

(defn feed [user-id limit offset]
  (let [query   {:select   [:a.* [:u.userId :currentUserId]]
                 :from     [[:article :a]]
                 :join     [[:userFollows :u] [:= :a.userId :u.followedUserId]]
                 :where    [:= :currentUserId user-id]
                 :order-by [[:a.updatedAt :desc] [:a.id :desc]]
                 :limit    limit
                 :offset   offset}
        results (jdbc/query (database/db) (sql/format query) {:identifiers identity})]
    results))
