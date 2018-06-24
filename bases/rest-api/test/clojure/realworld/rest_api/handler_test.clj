(ns clojure.realworld.rest-api.handler-test
  (:require [clojure.test :refer :all]
            [clojure.realworld.article.interface :as article]
            [clojure.realworld.rest-api.handler :as handler]
            [clojure.realworld.comment.interface :as comment-comp]
            [clojure.realworld.profile.interface :as profile]
            [clojure.realworld.spec.interface :as spec]
            [clojure.realworld.tag.interface :as tag]
            [clojure.realworld.user.interface :as user]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn prepare-for-tests [f]
  (with-redefs [user/login!                   (fn [_] [true {}])
                user/register!                (fn [_] [true {}])
                user/user-by-token            (fn [_] [true {}])
                user/update-user!             (fn [_ _] [true {}])
                profile/fetch-profile         (fn [_ _] [true {}])
                profile/follow!               (fn [_ _] [true {}])
                profile/unfollow!             (fn [_ _] [true {}])
                article/article               (fn [_ _] [true {}])
                article/create-article!       (fn [_ _] [true {}])
                article/update-article!       (fn [_ _ _] [true {}])
                article/delete-article!       (fn [_ _] [true {}])
                article/favorite-article!     (fn [_ _] [true {}])
                article/unfavorite-article!   (fn [_ _] [true {}])
                article/feed                  (fn [_ limit offset] [true {:limit limit :offset offset}])
                article/articles              (fn [_ limit offset author tag favorited]
                                               [true {:limit  limit :offset offset
                                                      :author author :tag tag :favorited favorited}])
                tag/all-tags                  (fn [] [true {:tags []}])
                comment-comp/article-comments (fn [_ _] [true {:comments []}])
                comment-comp/add-comment!     (fn [_ _ _] [true {}])
                comment-comp/delete-comment!  (fn [_ _] [true {}])]
    (f)))

(use-fixtures :each prepare-for-tests)

(deftest login--invalid-input--return-422
  (let [res (handler/login {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest login--valid-input--return-200
  (let [res (handler/login {:params {:user (gen/generate (s/gen user/login))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest register--invalid-input--return-422
  (let [res (handler/register {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest register--valid-input--return-200
  (let [res (handler/register {:params {:user (gen/generate (s/gen user/register))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest current-user--valid-input--return-200
  (let [auth-user (gen/generate (s/gen user/user))
        res       (handler/current-user {:auth-user auth-user})]
    (is (= {:status 200
            :body   {:user auth-user}}
           res))))

(deftest update-user--invalid-input--return-422
  (let [res (handler/update-user {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest update-user--valid-input--return-200
  (let [res (handler/update-user {:auth-user (gen/generate (s/gen user/user))
                                  :params    {:user (gen/generate (s/gen user/update-user))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest profile--invalid-input--return-422
  (let [res (handler/profile {})]
    (is (= {:status 422
            :body   {:errors {:username ["Invalid username."]}}}
           res))))

(deftest profile--valid-input--return-200
  (let [res (handler/profile {:auth-user (gen/generate (s/gen user/user))
                              :params    {:username (gen/generate (s/gen spec/username?))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest follow--invalid-input--return-422
  (let [res (handler/follow-profile {})]
    (is (= {:status 422
            :body   {:errors {:username ["Invalid username."]}}}
           res))))

(deftest follow--valid-input--return-200
  (let [res (handler/follow-profile {:auth-user (gen/generate (s/gen user/user))
                                     :params    {:username (gen/generate (s/gen spec/username?))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest unfollow--invalid-input--return-422
  (let [res (handler/unfollow-profile {})]
    (is (= {:status 422
            :body   {:errors {:username ["Invalid username."]}}}
           res))))

(deftest unfollow--valid-input--return-200
  (let [res (handler/unfollow-profile {:auth-user (gen/generate (s/gen user/user))
                                       :params    {:username (gen/generate (s/gen spec/username?))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest article--invalid-input--return-422
  (let [res (handler/article {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest article--valid-input--return-200
  (let [res (handler/article {:params {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest create-article--invalid-input--return-422
  (let [res (handler/create-article {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest create-article--valid-input--return-200
  (let [res (handler/create-article {:auth-user (gen/generate (s/gen user/user))
                                     :params    {:article (gen/generate (s/gen article/create-article))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest update-article--invalid-body--return-422
  (let [res (handler/update-article {:params {:slug "this-is-slug"}})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest update-article--invalid-slug--return-422
  (let [res (handler/update-article {:params {:article (gen/generate (s/gen article/update-article))}})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest update-article--valid-input--return-200
  (let [res (handler/update-article {:auth-user (gen/generate (s/gen user/user))
                                     :params    {:slug    "this-is-slug"
                                                 :article (gen/generate (s/gen article/update-article))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest delete-article--invalid-input--return-422
  (let [res (handler/delete-article {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest delete-article--valid-input--return-200
  (let [res (handler/delete-article {:auth-user (gen/generate (s/gen user/user))
                                     :params    {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest favorite-article--invalid-input--return-422
  (let [res (handler/favorite-article {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest favorite-article--valid-input--return-200
  (let [res (handler/favorite-article {:auth-user (gen/generate (s/gen user/user))
                                       :params    {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest unfavorite-article--invalid-input--return-422
  (let [res (handler/unfavorite-article {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest unfavorite-article--valid-input--return-200
  (let [res (handler/unfavorite-article {:auth-user (gen/generate (s/gen user/user))
                                         :params    {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest tags--return-200
  (let [res (handler/tags {})]
    (is (= {:status 200
            :body   {:tags []}}
           res))))

(deftest comments--invalid-input--return-422
  (let [res (handler/comments {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest comments--valid-input--return-200
  (let [res (handler/comments {:auth-user (gen/generate (s/gen user/user))
                               :params    {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {:comments []}}
           res))))

(deftest delete-comment--invalid-id-string--return-422
  (let [res (handler/delete-comment {:id "asd"})]
    (is (= {:status 422
            :body   {:errors {:id ["Invalid comment id."]}}}
           res))))

(deftest delete-comment--nil-id-string--return-422
  (let [res (handler/delete-comment {})]
    (is (= {:status 422
            :body   {:errors {:id ["Invalid comment id."]}}}
           res))))

(deftest delete-comment--valid-string-input--return-200
  (let [res (handler/delete-comment {:auth-user (gen/generate (s/gen user/user))
                                     :params    {:id "1"}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest delete-comment--valid-int-input--return-200
  (let [res (handler/delete-comment {:auth-user (gen/generate (s/gen user/user))
                                     :params    {:id 1}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest add-comment--invalid-slug--return-422
  (let [res (handler/add-comment {:auth-user (gen/generate (s/gen user/user))
                                  :params    {:comment (gen/generate (s/gen comment-comp/add-comment))}})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest add-comment--invalid-comment--return-422
  (let [res (handler/add-comment {:auth-user (gen/generate (s/gen user/user))
                                  :params    {:slug "this-is-slug"}})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest add-comment--valid-input--return-200
  (let [res (handler/add-comment {:auth-user (gen/generate (s/gen user/user))
                                  :params    {:slug    "this-is-slug"
                                              :comment (gen/generate (s/gen comment-comp/add-comment))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest feed--invalid-limit--return-200
  (let [res (handler/feed {:auth-user (gen/generate (s/gen user/user))
                           :params    {:limit  "invalid-limit"
                                       :offset 0}})]
    (is (= {:status 200
            :body   {:limit  nil
                     :offset 0}}
           res))))

(deftest feed--invalid-offset--return-200
  (let [res (handler/feed {:auth-user (gen/generate (s/gen user/user))
                           :params    {:offset "invalid-offset"
                                       :limit  10}})]
    (is (= {:status 200
            :body   {:limit  10
                     :offset nil}}
           res))))

(deftest feed--string-offset--return-200
  (let [res (handler/feed {:auth-user (gen/generate (s/gen user/user))
                           :params    {:offset "5"
                                       :limit  10}})]
    (is (= {:status 200
            :body   {:limit  10
                     :offset 5}}
           res))))

(deftest feed--string-limit--return-200
  (let [res (handler/feed {:auth-user (gen/generate (s/gen user/user))
                           :params    {:offset 5
                                       :limit  "10"}})]
    (is (= {:status 200
            :body   {:limit  10
                     :offset 5}}
           res))))

(deftest feed--valid-input--return-200
  (let [res (handler/feed {:auth-user (gen/generate (s/gen user/user))
                           :params    {:offset 5
                                       :limit  10}})]
    (is (= {:status 200
            :body   {:limit  10
                     :offset 5}}
           res))))

(deftest feed--no-limit-and-offset--return-200
  (let [res (handler/feed {:auth-user (gen/generate (s/gen user/user))
                           :params    {}})]
    (is (= {:status 200
            :body   {:limit  nil
                     :offset nil}}
           res))))

(deftest articles--invalid-limit--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:limit  "invalid-limit"
                                           :offset 0}})]
    (is (= {:status 200
            :body   {:limit     nil
                     :offset    0
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--invalid-offset--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset "invalid-offset"
                                           :limit  10}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    nil
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--string-offset--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset "5"
                                           :limit  10}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--string-limit--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset 5
                                           :limit  "10"}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--invalid-tag--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset 5
                                           :limit  10
                                           :tag    10}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--invalid-author--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset 5
                                           :limit  10
                                           :author 10}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--invalid-favorited--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset    5
                                           :limit     10
                                           :favorited 10}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--valid-filters--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset    5
                                           :limit     10
                                           :author    "author"
                                           :tag       "tag"
                                           :favorited "favorited"}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       "tag"
                     :author    "author"
                     :favorited "favorited"}}
           res))))

(deftest articles--valid-input--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {:offset 5
                                           :limit  10}})]
    (is (= {:status 200
            :body   {:limit     10
                     :offset    5
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))

(deftest articles--no-limit-and-offset--return-200
  (let [res (handler/articles {:auth-user (gen/generate (s/gen user/user))
                               :params    {}})]
    (is (= {:status 200
            :body   {:limit     nil
                     :offset    nil
                     :tag       nil
                     :author    nil
                     :favorited nil}}
           res))))
