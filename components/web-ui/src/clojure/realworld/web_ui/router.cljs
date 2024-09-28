(ns clojure.realworld.web-ui.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/router.cljs

(def routes
  "App Routes
   
   Define routes so that when we enter specific path the router knows what to
   show us. A route is simply a data structure--a vector--with a pattern and 
   a result."
  ["/" {""         :home
        "login"    :login
        "logout"   :logout
        "register" :register
        "settings" :settings
        "editor/"  {[:slug] :editor}
        "article/" {[:slug] :article}
        "profile/" {[:user-id] {""           :profile
                                "/favorites" :favorited}}}])

(defn create-history
  "Creates and returns a new history object.
   
   We need to know the history of our routes so that we can navigate back and 
   forward. For that we'll use `pushy/pushy`, to which we need to provide a dispatch
   function (what happens on dispatch) and match (what routes should we match).
   
   `dispatch-fn`: A single arity function that accepts a Clojure map as its argument. Called when 
                  the route changes with the details of the new route."
  [dispatch-fn]
  (let [dispatch #(dispatch-fn {:page      (:handler %)
                                :slug      (get-in % [:route-params :slug])
                                :profile   (get-in % [:route-params :user-id])
                                :favorited (get-in % [:route-params :user-id])})
        match #(bidi/match-route routes %)]
    
    ;; pushy is here to take care of nice looking urls. Normally we would have to
    ;; deal with #. By using pushy we can have '/about' instead of '/#/about'.
    ;; pushy takes three arguments:
    ;; dispatch-fn - which dispatches when a match is found
    ;; match-fn - which checks if a route exist
    ;; identity-fn (optional) - extract the route from value returned by match-fn
    (pushy/pushy dispatch match)))

;; Current history object stored in an atom. 
(defonce history (atom nil))

(defn start!
  "Creates a new history object with the given `dispatch-fn`, sets it as the current history
   object, and starts pushy."
  [dispatch-fn]
  (reset! history (create-history dispatch-fn))
  (pushy/start! @history))

(defn set-token!
  "To change route after some actions we will need to set url and for that we
   will use set-token!, taking a token."
  [token]
  (pushy/set-token! @history token))

(def url-for 
  "To dispatch routes in our UI (view) we will use url-for and then pass a
   keyword to which route we want to direct the user.
   
   usage: (url-for :home)"
  (partial bidi/path-for routes))
