(ns clojure.org.realworld.common.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str])
  (:import (java.util UUID)))

(def ^:private email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(def ^:private uri-regex #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")
(def ^:private slug-regex #"^[a-z0-9]+(?:-[a-z0-9]+)*$")

(s/def :core/non-empty-string (s/and string? not-empty))

(s/def :core/username (s/with-gen :core/non-empty-string
                                  #(gen/fmap (fn [_] (str (UUID/randomUUID)))
                                             (gen/string-alphanumeric))))

(s/def :core/email (s/with-gen (s/and string? #(re-matches email-regex %))
                               #(gen/fmap (fn [[s1 s2]] (str s1 "@" s2 ".com"))
                                          (gen/tuple (gen/string-alphanumeric) (gen/string-alphanumeric)))))

(s/def :core/uri (s/with-gen (s/and string? #(re-matches uri-regex %))
                             #(gen/fmap (fn [[c1 c2]]
                                          (let [s1 (apply str c1)
                                                s2 (apply str c2)]
                                            (str "http://" s1 "." (subs s2 0 (if (< 3 (count s2)) 3 (count s2))))))
                                        (gen/tuple (gen/vector (gen/char-alpha) 2 100) (gen/vector (gen/char-alpha) 2 5)))))

(s/def :core/slug (s/with-gen (s/and string? #(re-matches slug-regex %))
                              #(gen/fmap (fn [[c1 c2]]
                                           (let [s1 (str/lower-case (apply str c1))
                                                 s2 (str/lower-case (apply str c2))]
                                             (str s1 "-" s2)))
                                         (gen/tuple (gen/vector (gen/char-alpha) 2 10) (gen/vector (gen/char-alpha) 2 10)))))

(s/def :core/password (s/and string? #(<= 8 (count %))))

(s/def :user/id pos-int?)
(s/def :user/email :core/email)
(s/def :user/username :core/username)
(s/def :user/password :core/password)

(s/def :user/image (s/or :uri :core/uri
                         :nil nil?))

(s/def :user/bio (s/or :string :core/non-empty-string
                       :nil nil?))

(s/def :user/token :core/non-empty-string)

(s/def :core/login (s/keys :req-un [:user/email
                                    :user/password]))

(s/def :core/register (s/keys :req-un [:user/username
                                       :user/email
                                       :user/password]))

(s/def :core/update-user (s/keys :req-un [:user/email
                                          :user/username]
                                 :opt-un [:user/password
                                          :user/image
                                          :user/bio]))

(s/def :core/user (s/keys :req-un [:user/id
                                   :user/email
                                   :user/username]
                          :opt-un [:user/image
                                   :user/bio
                                   :user/token]))

(s/def :core/visible-user (s/keys :req-un [:core/user]))

(s/def :profile/username :user/username)
(s/def :profile/bio :user/bio)
(s/def :profile/image :user/image)
(s/def :profile/following boolean?)

(s/def :core/profile (s/keys :req-un [:profile/username
                                      :profile/following]
                             :opt-in [:profile/image
                                      :profile/bio]))

(s/def :article/id pos-int?)
(s/def :article/slug :core/slug)
(s/def :article/title :core/non-empty-string)
(s/def :article/description :core/non-empty-string)
(s/def :article/body :core/non-empty-string)
(s/def :article/createdAt string?)
(s/def :article/updatedAt string?)
(s/def :article/tagList (s/coll-of :core/non-empty-string :kind vector?))
(s/def :article/favorited boolean?)
(s/def :article/favoritesCount nat-int?)
(s/def :article/author :core/profile)

(s/def :core/create-article (s/keys :req-un [:article/title
                                             :article/description
                                             :article/body]
                                    :opt-un [:article/tagList]))

(s/def :core/update-article (s/keys :opt-un [:article/title
                                             :article/description
                                             :article/body]))

(s/def :core/article (s/keys :req-un [:article/slug
                                      :article/title
                                      :article/description
                                      :article/body
                                      :article/createdAt
                                      :article/updatedAt
                                      :article/favorited
                                      :article/favoritesCount
                                      :article/author]
                             :opt-un [:article/tagList]))

(s/def :core/visible-article (s/keys :req-un [:core/article]))

(s/def :comment/id pos-int?)
(s/def :comment/createdAt string?)
(s/def :comment/updatedAt string?)
(s/def :comment/body :core/non-empty-string)
(s/def :comment/author :core/profile)

(s/def :core/add-comment (s/keys :req-un [:comment/body]))

(s/def :core/comment (s/keys :req-un [:comment/id
                                      :comment/createdAt
                                      :comment/updatedAt
                                      :comment/body
                                      :comment/author]))
