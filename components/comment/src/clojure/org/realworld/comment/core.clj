(ns clojure.org.realworld.comment.core
  (:require [clj-time.core :as t]
            [clojure.org.realworld.article.interface :as article]
            [clojure.org.realworld.comment.store :as store]
            [clojure.org.realworld.profile.interface :as profile]))

(defn comment->visible-comment [auth-user comment]
  (let [user-id (:userId comment)
        [_ author] (profile/fetch-profile auth-user user-id)]
    (-> comment
        (dissoc :userId :articleId)
        (assoc :author (:profile author)))))

(defn article-comments [auth-user slug]
  (let [[ok? {:keys [article]}] (article/article auth-user slug)]
    (if ok?
      (let [article-id (:id article)
            comments   (store/comments article-id)
            res        (mapv #(comment->visible-comment auth-user %) comments)]
        [true {:comments res}])
      [false {:errors {:slug ["Cannot find an article with given slug."]}}])))

(defn add-comment! [auth-user slug {:keys [body]}]
  (let [[ok? {:keys [article]}] (article/article auth-user slug)]
    (if ok?
      (let [now        (t/now)
            comment    {:body      body
                        :articleId (:id article)
                        :userId    (:id auth-user)
                        :createdAt now
                        :updatedAt now}
            comment-id (store/add-comment! comment)]
        (if comment-id
          (let [added-comment (store/find-by-id comment-id)
                res           (comment->visible-comment auth-user added-comment)]
            [true {:comment res}])
          [false {:errors {:other ["Cannot insert comment into db."]}}]))
      [false {:errors {:slug ["Cannot find an article with given slug."]}}])))

(defn delete-comment! [auth-user id]
  (if-let [comment (store/find-by-id id)]
    (if (= (:id auth-user) (:userId comment))
      [true (store/delete-comment! id)]
      [false {:errors {:authorization ["You need to be author of this comment to delete it."]}}])
    [false {:errors {:id ["Cannot find a comment with given id."]}}]))
