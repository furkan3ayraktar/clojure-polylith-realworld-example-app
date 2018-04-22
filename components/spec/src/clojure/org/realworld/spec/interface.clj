(ns clojure.org.realworld.spec.interface
  (:require [clojure.org.realworld.spec.core :as core]))

(def username? core/username?)

(def non-empty-string? core/non-empty-string?)

(def email? core/email?)

(def uri-string? core/uri-string?)

(def slug? core/slug?)

(def password? core/password?)
