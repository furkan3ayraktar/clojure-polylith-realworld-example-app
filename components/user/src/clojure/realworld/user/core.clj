(ns clojure.realworld.user.core
  (:require [clojure.realworld.user.store :as store]
            [crypto.password.pbkdf2 :as crypto]
            [clj-jwt.core :as jwt]
            [clj-time.core :as t]
            [environ.core :refer [env]]))

(defn- token-secret []
  (if (contains? env :secret)
    (env :secret)
    "some-default-secret-do-not-use-it"))

(defn- generate-token [email]
  (let [now   (t/now)
        claim {:iss email
               :exp (t/plus now (t/days 7))
               :iat now}]
    (-> claim jwt/jwt (jwt/sign :HS256 (token-secret)) jwt/to-str)))

(defn encrypt-password [password]
  (-> password crypto/encrypt str))

(defn user->visible-user [user]
  {:user (dissoc user :password)})

(defn login! [{:keys [email password]}]
  (if-let [user (store/find-by-email email)]
    (if (crypto/check password (:password user))
      (let [new-token (generate-token email)
            _         (store/update-token! email new-token)
            new-user  (assoc user :token new-token)]
        [true (user->visible-user new-user)])
      [false {:errors {:password ["Invalid password."]}}])
    [false {:errors {:email ["Invalid email."]}}]))

(defn register! [{:keys [username email password]}]
  (if-let [_ (store/find-by-email email)]
    [false {:errors {:email ["A user exists with given email."]}}]
    (if-let [_ (store/find-by-username username)]
      [false {:errors {:username ["A user exists with given username."]}}]
      (let [user-input {:email    email
                        :username username
                        :password (encrypt-password password)
                        :token    (generate-token email)}
            _          (store/insert-user! user-input)]
        (if-let [user (store/find-by-email email)]
          [true (user->visible-user user)]
          [false {:errors {:other ["Cannot insert user into db."]}}])))))

(defn user-by-token [token]
  (if-let [user (store/find-by-token token)]
    [true (user->visible-user user)]
    [false {:errors {:token ["Cannot find a user with associated token."]}}]))

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
                                  :username (when username username)
                                  :token (when (or email password) (generate-token email-to-use))})
            user-input   (merge {:image image
                                 :bio   bio}
                                optional-map)
            _            (store/update-user! (:id auth-user) user-input)]
        (if-let [updated-user (store/find-by-email email-to-use)]
          [true (user->visible-user updated-user)]
          [false {:errors {:other ["Cannot update user."]}}])))))
