(ns clojure.realworld.shared-ui.interface
  (:require [clojure.realworld.shared-ui.views :as views]))

;; -- Common UI Components ---------------------------------------------------
;;
(defn format-date
  "Format a date for display"
  [date]
  (views/format-date date))

(defn tags-list
  "Render a list of tags"
  [tags-list]
  [views/tags-list tags-list])

(defn article-meta
  "Render article metadata (author, date, actions)"
  [article]
  [views/article-meta article])

(defn articles-preview
  "Render a preview of an article"
  [article]
  [views/articles-preview article])

(defn articles-list
  "Render a list of articles"
  [articles loading-articles]
  [views/articles-list articles loading-articles])

(defn errors-list
  "Render a list of errors"
  [errors]
  [views/errors-list errors])

;; -- Layout Components ------------------------------------------------------
;;
(defn header
  "Render the application header"
  []
  [views/header])

(defn footer
  "Render the application footer"
  []
  [views/footer]) 
