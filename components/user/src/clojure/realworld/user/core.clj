(ns clojure.realworld.user.core
  (:require [buddy.sign.jwt :as jwt]
            [cljc.java-time.instant :as i]
            [cljc.java-time.zoned-date-time :as zdt]
            [clojure.realworld.env.interface :as env]
            [clojure.realworld.user.store :as store]
            [crypto.password.pbkdf2 :as crypto]))

(defn- token-secret []
  (if (contains? env/env :secret)
    (env/env :secret)
    "some-default-secret-do-not-use-it"))

(defn- generate-token [email username]
  (let [now   (zdt/now)
        claim {:sub username
               :iss email
               :exp (zdt/to-instant (zdt/plus-days now 7))
               :iat (zdt/to-instant now)}]
    (jwt/sign claim (token-secret) {:alg :hs256})))

(defn- expired? [jwt]
  (if-let [exp (:exp jwt)]
    (try
      (i/is-before (i/of-epoch-second exp) (i/now))
      (catch Exception _
        true))
    true))

(defn- token->claims [jwt-string]
  (when-let [claims (jwt/unsign jwt-string (token-secret) {:skip-validation true})]
    (when-not (expired? claims)
      claims)))

(fn []

  (token->claims (generate-token "a@b.com" "user"))

  (jwt/unsign "token" (token-secret) {:skip-validation true})

  (-> (generate-token "a@b.com" "user")
      (jwt/unsign (token-secret) {:skip-validation true})
      expired?
      )

  )

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
  (if (store/find-by-email email)
    [false {:errors {:email ["A user exists with given email."]}}]
    (if (store/find-by-username username)
      [false {:errors {:username ["A user exists with given username."]}}]
      (do
        (store/insert-user! {:email    email
                             :username username
                             :password (encrypt-password password)})
        (if-let [user (store/find-by-email email)]
          [true (user->visible-user user (generate-token email username))]
          [false {:errors {:other ["Cannot insert user into db."]}}])))))

(defn user-by-token [token]
  (let [claims   (try
                   (token->claims token)
                   (catch Exception _
                     nil))
        username (:sub claims)]
    (if-let [user (store/find-by-username username)]
      [true (user->visible-user user token)]
      [false {:errors {:token ["Cannot find a user with associated token."]}}])))

(defn update-user! [auth-user {:keys [username email password image bio]}]
  (if (and (some? email)
           (not= email (:email auth-user))
           (some? (store/find-by-email email)))
    [false {:errors {:email ["A user exists with given email."]}}]
    (if (and (some? username)
             (not= username (:username auth-user))
             (some? (store/find-by-username username)))
      [false {:errors {:username ["A user exists with given username."]}}]
      (let [email-to-use (or email (:email auth-user))
            optional-map (filter #(-> % val some?)
                                 {:password (some-> password encrypt-password)
                                  :email    (when email email)
                                  :username (when username username)})
            user-input   (merge {:image image
                                 :bio   bio}
                                optional-map)]
        (store/update-user! (:id auth-user) user-input)
        (if-let [updated-user (store/find-by-email email-to-use)]
          [true (user->visible-user updated-user (:token auth-user))]
          [false {:errors {:other ["Cannot update user."]}}])))))
