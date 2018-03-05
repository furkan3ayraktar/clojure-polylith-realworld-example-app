(ns clojure.org.realworld.user.core
  (:require [clojure.java.jdbc :as jdbc]
            [java-jdbc.sql :as sql]
            [clojure.org.realworld.database.interface :as database]
            [crypto.password.pbkdf2 :as crypto]
            [clj-jwt.core :as jwt]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(declare find-by-token)

(defn- token-secret []
  (if (contains? env :secret)
    (env :secret)
    "some-default-secret-dont-use-it"))

(defn encrypt-password [password]
  (-> password crypto/encrypt str))

(defn generate-token [email]
  (let [now   (t/now)
        claim {:iss email
               :exp (t/plus now (t/days 7))
               :iat now}]
    (-> claim jwt/jwt (jwt/sign :HS256 (token-secret)) jwt/to-str)))

(defn- prepare-user [result-map key val]
  "Prepares the user data before updates"
  (cond
    (= key :password) (assoc result-map :password (encrypt-password val))
    (= key :token) result-map
    :else (assoc result-map key val)))

(defn update-token [email]
  (let [new-tok (generate-token email)]
    (if
      (not
        (nil? (jdbc/update! (database/db) :users {:token new-tok} ["email=?" email])))
      new-tok
      nil)))

(defn insert-user [user]
  (let [db (database/db)
        email (user :email)
        username (user :username)
        bio (if (contains? user :bio) (user :bio) "")
        image ""
        password (-> user :password encrypt-password)
        token (generate-token email)]
    (jdbc/insert! db
                  :users
                  {:email email
                   :username username
                   :bio bio
                   :image image
                   :token token
                   :password password})))

(defn update-by-token [token user-map]
  (let [prepared-user (reduce-kv prepare-user {} user-map)
        db (database/db)]
    (jdbc/update! db :users prepared-user ["token = ?" token])))

(defn find-by-email-password [email password]
  "Finds an user by it's email and password."
  (let [results (jdbc/query (database/db)
                            (sql/select * :users (sql/where {:email email})))
        user (first results)]
    (if (crypto/check password (user :password)) user nil)))

(defn find-by-token [token]
  "Finds an user by it's token."
  (let [results (jdbc/query (database/db)
                            (sql/select * :users (sql/where {:token token})))
        user (first results)]
    user))
