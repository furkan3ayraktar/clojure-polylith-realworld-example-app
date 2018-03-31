(ns clojure.org.realworld.article.store
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.org.realworld.database.interface :as database]
            [java-jdbc.sql :as sql]))

(defn find-by-slug [slug]
  (let [results (jdbc/query (database/db)
                            (sql/select * :article (sql/where {:slug slug}))
                            {:identifiers identity})]
    (first results)))

(defn insert-article! [article-input]
  (jdbc/insert! (database/db) :article article-input {:entities identity}))

(defn tags-with-names [tag-names]
  (let [results (jdbc/query (database/db)
                            (sql/select * :tag
                                        (sql/where {:name tag-names})))]
    results))

(defn add-tags-to-article! [article-id tag-names]
  (when-not (empty? tag-names)
    (let [tags (tags-with-names tag-names)
          inputs (mapv #(hash-map :articleId article-id
                                  :tagId (:id %))
                       tags)]
      (jdbc/insert-multi! (database/db) :articleTags inputs))))

(defn article-tags [article-id]
  (let [results (jdbc/query (database/db)
                            (sql/select [:name] :articleTags
                                        (sql/join :tag {:tagId :id})
                                        (sql/where {:articleId article-id})))]
    (mapv :name results)))

(defn insert-tag! [name]
  (let [tag (first (tags-with-names name))]
    (when-not tag
      (jdbc/insert! (database/db) :tag {:name name}))))

(defn update-tags! [tag-names]
  (when-not (empty? tag-names)
    (doseq [name tag-names]
      (insert-tag! name))))

(defn favorited? [user-id article-id]
  (let [result (jdbc/query (database/db)
                           (sql/select "count(*)" :favoriteArticles
                                       (sql/where {:articleId article-id
                                                   :userId user-id})))]
    (< 0 (-> result first first val))))

(defn favorites-count [article-id]
  (let [result (jdbc/query (database/db)
                           (sql/select "count(*)" :favoriteArticles
                                       (sql/where {:articleId article-id})))]
    (-> result first first val)))

(defn update-article! [id article-input]
  (jdbc/update! (database/db)
                :article
                article-input
                (sql/where {:id id})))

(defn delete-article! [id]
  (jdbc/delete! (database/db) :articleTags (sql/where {:articleId id}))
  (jdbc/delete! (database/db) :favoriteArticles (sql/where {:articleId id}))
  (jdbc/delete! (database/db) :article (sql/where {:id id}))
  nil)
