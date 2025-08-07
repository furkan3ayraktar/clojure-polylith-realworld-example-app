(ns clojure.realworld.article-ui.interface
  (:require [clojure.realworld.article-ui.views :as views]))

;; -- Article UI Components --------------------------------------------------
;;
(defn profile
  "Render the user profile page"
  []
  [views/profile])

(defn editor
  "Render the article editor page"
  []
  [views/editor])

(defn article
  "Render the article view page"
  []
  [views/article]) 
