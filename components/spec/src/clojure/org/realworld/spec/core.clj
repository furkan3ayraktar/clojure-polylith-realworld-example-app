(ns clojure.org.realworld.spec.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [spec-tools.core :as st])
  (:import (java.util UUID)))

(def ^:private email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(def ^:private uri-regex #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")
(def ^:private slug-regex #"^[a-z0-9]+(?:-[a-z0-9]+)*$")

(def non-empty-string?
  (st/spec {:spec        (s/and string? #(not (str/blank? %)))
            :type        :string
            :description "Non empty string spec. Checks with clojure.string/blank?"}))

(def username?
  (st/spec {:spec        non-empty-string?
            :type        :string
            :description "A non empty string spec with a special username (UUID) generator."
            :gen         #(gen/fmap (fn [_] (str (UUID/randomUUID)))
                                    (gen/string-alphanumeric))}))

(def email?
  (st/spec {:spec        (s/and string? #(re-matches email-regex %))
            :type        :string
            :description "A string spec that conforms to email-regex."
            :gen         #(gen/fmap (fn [[s1 s2]] (str s1 "@" s2 ".com"))
                                    (gen/tuple (gen/string-alphanumeric) (gen/string-alphanumeric)))}))

(def uri-string?
  (st/spec {:spec        (s/and string? #(re-matches uri-regex %))
            :type        :string
            :description "A string spec that conforms to uri-regex."
            :gen         #(gen/fmap (fn [[c1 c2]]
                                      (let [s1 (apply str c1)
                                            s2 (apply str c2)]
                                        (str "http://" s1 "." (subs s2 0 (if (< 3 (count s2)) 3 (count s2))))))
                                    (gen/tuple (gen/vector (gen/char-alpha) 2 100) (gen/vector (gen/char-alpha) 2 5)))}))

(def slug?
  (st/spec {:spec        (s/and string? #(re-matches slug-regex %))
            :type        :string
            :description "A string spec that conforms to slug-regex."
            :gen         #(gen/fmap (fn [[c1 c2]]
                                      (let [s1 (str/lower-case (apply str c1))
                                            s2 (str/lower-case (apply str c2))]
                                        (str s1 "-" s2)))
                                    (gen/tuple (gen/vector (gen/char-alpha) 2 10) (gen/vector (gen/char-alpha) 2 10)))}))

(def password?
  (st/spec {:spec        (s/and string? #(<= 8 (count %)))
            :type        :string
            :description "A string spec with more than or equal to 8 characters."}))

;; Profile specs

(s/def :profile/username username?)
(s/def :profile/bio (s/or :string non-empty-string?
                          :nil nil?))
(s/def :profile/image (s/or :uri uri-string?
                            :nil nil?))
(s/def :profile/following boolean?)

(s/def :core/profile (s/keys :req-un [:profile/username
                                      :profile/following]
                             :opt-in [:profile/image
                                      :profile/bio]))

;; Article specs

(s/def :article/id pos-int?)
(s/def :article/slug slug?)
(s/def :article/title non-empty-string?)
(s/def :article/description non-empty-string?)
(s/def :article/body non-empty-string?)
(s/def :article/createdAt string?)
(s/def :article/updatedAt string?)
(s/def :article/tagList (s/coll-of non-empty-string? :kind vector?))
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

;; Comment specs

(s/def :comment/id pos-int?)
(s/def :comment/createdAt string?)
(s/def :comment/updatedAt string?)
(s/def :comment/body non-empty-string?)
(s/def :comment/author :core/profile)

(s/def :core/add-comment (s/keys :req-un [:comment/body]))

(s/def :core/comment (s/keys :req-un [:comment/id
                                      :comment/createdAt
                                      :comment/updatedAt
                                      :comment/body
                                      :comment/author]))

(s/def :core/visible-comment (s/keys :req-un [:core/comment]))
