(ns clojure.org.realworld.article.core
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.org.realworld.article.store :as store]
            [clojure.org.realworld.profile.interface :as profile]
            [slugger.core :as slugger]))

(defn article->visible-article [article auth-user]
  (let [user-id (:userId article)
        article-id (:id article)
        [_ author] (profile/profile auth-user user-id)
        favorited? (if auth-user (store/favorited? (:id auth-user) article-id) false)
        favorites-count (store/favorites-count article-id)
        tags (store/article-tags article-id)]
    (assoc (dissoc article :userId)
      :favorited favorited?
      :favoritesCount favorites-count
      :author (:profile author)
      :tagList tags)))

(defn- create-slug [title now]
  (when title
    (let [slug          (slugger/->slug title)]
      (if (store/find-by-slug slug)
        (str slug "-" (c/to-long now))
        slug))))

(defn article [auth-user slug]
  (if-let [article (store/find-by-slug slug)]
    [true {:article (article->visible-article article auth-user)}]
    [false {:errors {:slug ["Cannot find an article with given slug."]}}]))

(defn create-article! [auth-user {:keys [title description body tagList]}]
  (let [user-id       (:id auth-user)
        now           (t/now)
        slug          (create-slug title now)
        article-input {:slug        slug
                       :title       title
                       :description description
                       :body        body
                       :createdAt   now
                       :updatedAt   now
                       :userId      user-id}
        _             (store/insert-article! article-input)]
    (if-let [article (store/find-by-slug slug)]
      (let [article-id (:id article)
            _          (store/update-tags! tagList)
            _          (store/add-tags-to-article! article-id tagList)]
        [true {:article (article->visible-article article auth-user)}])
      [false {:errors {:other ["Cannot insert article into db."]}}])))

(defn update-article! [auth-user slug {:keys [title description body]}]
  (if-let [article (store/find-by-slug slug)]
    (if (= (:id auth-user) (:userId article))
      (let [now (t/now)
            slug (create-slug title now)
            article-input (into {} (filter #(-> % val nil? not)
                                           {:slug slug
                                            :title title
                                            :description description
                                            :body body
                                            :updatedAt now}))
            _ (store/update-article! (:id article) article-input)]
        (if-let [updated-article (store/find-by-slug (if slug slug (:slug article)))]
          [true {:article (article->visible-article updated-article auth-user)}]
          [false {:errors {:other ["Cannot insert article into db."]}}]))
      [false {:errors {:authorization ["You need to be author of this article to update it."]}}])
    [false {:errors {:slug ["Cannot find an article with given slug."]}}]))

(defn delete-article! [auth-user slug]
  (if-let [article (store/find-by-slug slug)]
    (if (= (:id auth-user) (:userId article))
      [true (store/delete-article! (:id article))]
      [false {:errors {:authorization ["You need to be author of this article to delete it."]}}])
    [false {:errors {:slug ["Cannot find an article with given slug."]}}]))

(defn favorite-article! [auth-user slug]
  (if-let [article (store/find-by-slug slug)]
    (do
      (store/favorite! (:id auth-user) (:id article))
      [true {:article (article->visible-article article auth-user)}])
    [false {:errors {:slug ["Cannot find an article with given slug."]}}]))

(defn unfavorite-article! [auth-user slug]
  (if-let [article (store/find-by-slug slug)]
    (do
      (store/unfavorite! (:id auth-user) (:id article))
      [true {:article (article->visible-article article auth-user)}])
    [false {:errors {:slug ["Cannot find an article with given slug."]}}]))

(defn feed [auth-user limit offset]
  (let [limit (or limit 20)
        offset (or offset 0)
        user-id (:id auth-user)
        articles (store/feed user-id limit offset)
        visible-articles (mapv #(article->visible-article % auth-user) articles)
        res {:articles visible-articles
             :articlesCount (count visible-articles)}]
    [true res]))
