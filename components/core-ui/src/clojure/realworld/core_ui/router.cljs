(ns clojure.realworld.core-ui.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/router.cljs

;; -- Routes ----------------------------------------------------------------
;;
(def routes
  ["/" {"" :home
        "login" :login
        "register" :register
        "settings" :settings
        "editor" {"" :editor
                 "/" :editor
                 ["/" :slug] :editor}
        "article" {["/" :slug] :article}
        "profile" {["/" :user-id] :profile
                   ["/" :user-id "/favorites"] :favorited}}])

;; -- Navigation ------------------------------------------------------------
;;
(defn url-for
  "Generate a URL for a given route and params"
  [route & params]
  (apply bidi/path-for routes route params))

;; -- History --------------------------------------------------------------
;;
(defn create-history
  "Create a history instance with the given dispatch function"
  [dispatch-fn]
  (pushy/pushy dispatch-fn
               (fn [x]
                 (when x
                   (let [match (bidi/match-route routes x)]
                     (when match
                       {:page (keyword (:handler match))
                        :slug (:slug match)
                        :user-id (:user-id match)}))))))

(def ^:private history-instance (atom nil))

(defn set-token!
  "Set the browser history token"
  [token]
  (when @history-instance
    (pushy/set-token! @history-instance token)))

(defn start!
  "Start the router with the given dispatch function"
  [dispatch-fn]
  (let [history (create-history dispatch-fn)]
    (reset! history-instance history)
    (pushy/start! history)))
