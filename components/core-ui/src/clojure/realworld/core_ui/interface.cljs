(ns clojure.realworld.core-ui.interface
  (:require [clojure.realworld.core-ui.events :as events]
            [clojure.realworld.core-ui.router :as router]
            [clojure.realworld.core-ui.subs :as subs]))

;; -- Router Functions --------------------------------------------------------
;;
(defn url-for
  "Generate a URL for a given route and params"
  [route & params]
  (apply router/url-for route params))

(defn start-router!
  "Starts the application router with the given dispatch function.
   
   `dispatch-fn`: A single arity function that accepts a Clojure map as its argument. Called when 
                  the route changes with the details of the new route."
  [dispatch-fn]
  (router/start! dispatch-fn))

(def set-token! router/set-token!)

;; -- Event Functions --------------------------------------------------------
;;
(def >initialise-db events/>initialise-db)
(def >set-active-page events/>set-active-page)
(def >reset-active-article events/>reset-active-article)
(def >set-active-article events/>set-active-article)
(def >get-articles events/>get-articles)
(def >get-articles-success events/>get-articles-success)
(def >get-article events/>get-article)
(def >get-article-success events/>get-article-success)
(def >upsert-article events/>upsert-article)
(def >upsert-article-success events/>upsert-article-success)
(def >delete-article events/>delete-article)
(def >delete-article-success events/>delete-article-success)
(def >get-feed-articles events/>get-feed-articles)
(def >get-feed-articles-success events/>get-feed-articles-success)
(def >get-tags events/>get-tags)
(def >get-tags-success events/>get-tags-success)
(def >get-article-comments events/>get-article-comments)
(def >get-article-comments-success events/>get-article-comments-success)
(def >post-comment events/>post-comment)
(def >post-comment-success events/>post-comment-success)
(def >delete-comment events/>delete-comment)
(def >delete-comment-success events/>delete-comment-success)
(def >get-user-profile events/>get-user-profile)
(def >get-user-profile-success events/>get-user-profile-success)
(def >login events/>login)
(def >login-success events/>login-success)
(def >register-user events/>register-user)
(def >register-user-success events/>register-user-success)
(def >logout events/>logout)
(def >update-user events/>update-user)
(def >update-user-success events/>update-user-success)
(def >toggle-follow-user events/>toggle-follow-user)
(def >toggle-follow-user-success events/>toggle-follow-user-success)
(def >toggle-favorite-article events/>toggle-favorite-article)
(def >toggle-favorite-article-success events/>toggle-favorite-article-success)
(def >api-request-error events/>api-request-error)

;; -- Subscription Functions -------------------------------------------------
;;
(def <active-page subs/<active-page)
(def <articles subs/<articles)
(def <articles-count subs/<articles-count)
(def <active-article subs/<active-article)
(def <tags subs/<tags)
(def <comments subs/<comments)
(def <profile subs/<profile)
(def <loading subs/<loading)
(def <filter subs/<filter)
(def <active-filter subs/<active-filter)
(def <errors subs/<errors)
(def <user subs/<user)

;; -- Helper Functions ------------------------------------------------------
;;
(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (events/endpoint params))

(defn auth-header
  "Get user token and format for API authorization"
  [db]
  (events/auth-header db))
