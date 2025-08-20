(ns clojure.realworld.shared.interface
  (:require [clojure.realworld.shared.string-utils :as string-utils]))

(defn non-blank? 
  "Check if string is not blank - works identically in both environments"
  [string]
  (string-utils/non-blank? string))


(defn clean-tags 
  "Clean tag input by removing blank values"
  [tag-input]
  (string-utils/clean-tags tag-input))
