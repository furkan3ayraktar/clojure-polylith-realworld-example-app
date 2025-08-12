(ns clojure.realworld.shared.interface
  (:require [clojure.realworld.shared.string-utils :as string-utils]))

(defn non-blank? [s]
  "Check if string is not blank - works identically in both environments"
  (string-utils/non-blank? s))


(defn clean-tags [tag-input]
   "Clean tag input by removing blank values"
   (string-utils/clean-tags tag-input))
