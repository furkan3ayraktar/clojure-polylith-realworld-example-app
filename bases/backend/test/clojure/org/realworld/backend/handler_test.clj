(ns clojure.org.realworld.backend.handler-test
  (:require [clojure.test :refer :all]
            [clojure.org.realworld.article.interface :as article]
            [clojure.org.realworld.backend.handler :as handler]
            [clojure.org.realworld.profile.interface :as profile]
            [clojure.org.realworld.user.interface :as user]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn prepare-for-tests [f]
  (with-redefs [user/login              (fn [_] [true {}])
                user/register!          (fn [_] [true {}])
                user/user-by-token      (fn [_] [true {}])
                user/update-user!       (fn [_ _] [true {}])
                profile/profile         (fn [_ _] [true {}])
                profile/follow!         (fn [_ _] [true {}])
                profile/unfollow!       (fn [_ _] [true {}])
                article/article         (fn [_ _] [true {}])
                article/create-article! (fn [_ _] [true {}])
                article/update-article! (fn [_ _ _] [true {}])
                article/delete-article! (fn [_ _] [true {}])]
    (f)))

(use-fixtures :each prepare-for-tests)

(deftest login--invalid-input--return-422
  (let [res (handler/login {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest login--valid-input--return-200
  (let [res (handler/login {:params {:user (gen/generate (s/gen :core/login))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest register--invalid-input--return-422
  (let [res (handler/register {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest register--valid-input--return-200
  (let [res (handler/register {:params {:user (gen/generate (s/gen :core/register))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest current-user--valid-input--return-200
  (let [auth-user (gen/generate (s/gen :core/user))
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
  (let [res (handler/update-user {:auth-user (gen/generate (s/gen :core/user))
                                  :params    {:user (gen/generate (s/gen :core/update-user))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest profile--invalid-input--return-422
  (let [res (handler/profile {})]
    (is (= {:status 422
            :body   {:errors {:username ["Invalid username."]}}}
           res))))

(deftest profile--valid-input--return-200
  (let [res (handler/profile {:auth-user (gen/generate (s/gen :core/user))
                              :params    {:username (gen/generate (s/gen :core/username))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest follow--invalid-input--return-422
  (let [res (handler/follow-profile {})]
    (is (= {:status 422
            :body   {:errors {:username ["Invalid username."]}}}
           res))))

(deftest follow--valid-input--return-200
  (let [res (handler/follow-profile {:auth-user (gen/generate (s/gen :core/user))
                                     :params    {:username (gen/generate (s/gen :core/username))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest unfollow--invalid-input--return-422
  (let [res (handler/unfollow-profile {})]
    (is (= {:status 422
            :body   {:errors {:username ["Invalid username."]}}}
           res))))

(deftest unfollow--valid-input--return-200
  (let [res (handler/unfollow-profile {:auth-user (gen/generate (s/gen :core/user))
                                       :params    {:username (gen/generate (s/gen :core/username))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest article--invalid-input--return-422
  (let [res (handler/article {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest article--valid-input--return-200
  (let [res (handler/article {:params    {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest create-article--invalid-input--return-422
  (let [res (handler/create-article {})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest create-article--valid-input--return-200
  (let [res (handler/create-article {:auth-user (gen/generate (s/gen :core/user))
                                     :params    {:article (gen/generate (s/gen :core/create-article))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest update-article--invalid-body--return-422
  (let [res (handler/update-article {:params {:slug "this-is-slug"}})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest update-article--invalid-slug--return-422
  (let [res (handler/update-article {:params {:article (gen/generate (s/gen :core/update-article))}})]
    (is (= {:status 422
            :body   {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest update-article--valid-input--return-200
  (let [res (handler/update-article {:auth-user (gen/generate (s/gen :core/user))
                                     :params    {:slug "this-is-slug"
                                                 :article (gen/generate (s/gen :core/update-article))}})]
    (is (= {:status 200
            :body   {}}
           res))))

(deftest delete-article--invalid-input--return-422
  (let [res (handler/delete-article {})]
    (is (= {:status 422
            :body   {:errors {:slug ["Invalid slug."]}}}
           res))))

(deftest delete-article--valid-input--return-200
  (let [res (handler/delete-article {:auth-user (gen/generate (s/gen :core/user))
                                     :params    {:slug "this-is-slug"}})]
    (is (= {:status 200
            :body   {}}
           res))))
