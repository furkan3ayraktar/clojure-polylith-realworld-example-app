(ns clojure.org.realworld.backend.handler-test
  (:require [clojure.test :refer :all]
            [clojure.org.realworld.backend.handler :as handler]
            [clojure.org.realworld.user.interface :as user]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn prepare-for-tests [f]
  (with-redefs [user/login         (fn [_] [true {}])
                user/register!     (fn [_] [true {}])
                user/user-by-token (fn [_] [true {}])
                user/update-user!  (fn [_ _] [true {}])]

    (f)))

(use-fixtures :each prepare-for-tests)

(deftest login--invalid-input--return-422
  (let [res (handler/login {})]
    (is (= {:status 422
            :body {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest login--valid-input--return-200
  (let [res (handler/login {:params {:user (gen/generate (s/gen :core/login))}})]
    (is (= {:status 200
            :body {:user {}}}
           res))))

(deftest register--invalid-input--return-422
  (let [res (handler/register {})]
    (is (= {:status 422
            :body {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest register--valid-input--return-200
  (let [res (handler/register {:params {:user (gen/generate (s/gen :core/register))}})]
    (is (= {:status 200
            :body {:user {}}}
           res))))

(deftest current-user--valid-input--return-200
  (let [res (handler/current-user {:auth-token "token"})]
    (is (= {:status 200
            :body {:user {}}}
           res))))

(deftest update-user--invalid-input--return-422
  (let [res (handler/update-user {})]
    (is (= {:status 422
            :body {:errors {:body ["Invalid request body."]}}}
           res))))

(deftest update-user--valid-input--return-200
  (let [res (handler/update-user {:auth-token "token"
                                  :params {:user (gen/generate (s/gen :core/update-user))}})]
    (is (= {:status 200
            :body {:user {}}}
           res))))
