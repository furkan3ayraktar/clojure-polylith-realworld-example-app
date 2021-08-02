(ns clojure.realworld.user.core
  (:require [clojure.realworld.user.store :as store]
            [crypto.password.pbkdf2 :as crypto]
            [clj-jwt.core :as jwt]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.realworld.env.interface :as env]))

(defn- token-secret []
  (if (contains? env/env :secret)
    (env/env :secret)
    "some-default-secret-do-not-use-it"))

(defn- generate-token [email username]
  (let [now (t/now)
        claim {:sub username
               :iss email
               :exp (t/plus now (t/days 7))
               :iat now}]
    (-> claim jwt/jwt (jwt/sign :HS256 (token-secret)) jwt/to-str)))

(defn- jwt-str->jwt [jwt-string]
  (try
    (jwt/str->jwt jwt-string)
    (catch Exception _
      nil)))

(defn- expired? [jwt]
  (if-let [exp (-> jwt :claims :exp)]
    (try
      (t/before? (c/from-long (* 1000 exp)) (t/now))
      (catch Exception _
        true))
    true))

(defn- token->claims [jwt-string]
  (when-let [jwt (jwt-str->jwt jwt-string)]
    (when (and (jwt/verify jwt (token-secret))
               (not (expired? jwt)))
      (:claims jwt))))

(defn encrypt-password [password]
  (-> password crypto/encrypt str))

(defn user->visible-user [user token]
  {:user (-> user
             (assoc :token token)
             (dissoc :password))})

(defn login! [{:keys [email password]}]
  (if-let [user (store/find-by-email email)]
    (if (crypto/check password (:password user))
      (let [new-token (generate-token email (:username user))]
        [true (user->visible-user user new-token)])
      [false {:errors {:password ["Invalid password."]}}])
    [false {:errors {:email ["Invalid email."]}}]))

(defn register! [{:keys [username email password]}]
  (if-let [_ (store/find-by-email email)]
    [false {:errors {:email ["A user exists with given email."]}}]
    (if-let [_ (store/find-by-username username)]
      [false {:errors {:username ["A user exists with given username."]}}]
      (let [new-token (generate-token email username)
            user-input {:email    email
                        :username username
                        :password (encrypt-password password)}
            _ (store/insert-user! user-input)]
        (if-let [user (store/find-by-email email)]
          [true (user->visible-user user new-token)]
          [false {:errors {:other ["Cannot insert user into db."]}}])))))

(defn user-by-token [token]
  (let [claims (token->claims token)
        username (:sub claims)
        user (store/find-by-username username)]
    (if user
      [true (user->visible-user user token)]
      [false {:errors {:token ["Cannot find a user with associated token."]}}])))

(defn update-user! [auth-user {:keys [username email password image bio]}]
  (if (and (not (nil? email))
           (not= email (:email auth-user))
           (not (nil? (store/find-by-email email))))
    [false {:errors {:email ["A user exists with given email."]}}]
    (if (and (not (nil? username))
             (not= username (:username auth-user))
             (not (nil? (store/find-by-username username))))
      [false {:errors {:username ["A user exists with given username."]}}]
      (let [email-to-use (if email email (:email auth-user))
            optional-map (filter #(-> % val nil? not)
                                 {:password (when password (encrypt-password password))
                                  :email    (when email email)
                                  :username (when username username)})
            user-input (merge {:image image
                               :bio   bio}
                              optional-map)
            _ (store/update-user! (:id auth-user) user-input)]
        (if-let [updated-user (store/find-by-email email-to-use)]
          [true (user->visible-user updated-user (:token auth-user))]
          [false {:errors {:other ["Cannot update user."]}}])))))
