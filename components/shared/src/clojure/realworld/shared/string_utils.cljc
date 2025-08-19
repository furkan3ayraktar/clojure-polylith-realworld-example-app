(ns clojure.realworld.shared.string-utils
  (:require [clojure.string :as str]))

(defn non-blank? [string]
  (not (str/blank? string)))

(defn clean-tags [tag-input]
  (cond
    (string? tag-input) 
    (filter non-blank? (str/split tag-input #"\s+"))
    (sequential? tag-input) 
    (filter non-blank? tag-input)
    :else []))



