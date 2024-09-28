(ns clojure.realworld.web-ui.interface
  (:require [clojure.realworld.web-ui.views :as views]
            [clojure.realworld.web-ui.router :as router]
            [clojure.realworld.web-ui.events :as events]
            [clojure.realworld.web-ui.subs :as subs]))

(defn app 
  "Renders the application. The router will route to
   correct page through this entry point."
  []
  [views/app])

(defn start-router! 
  "Starts the application router with the given dispatch function.
   
   `dispatch-fn`: A single arity function that accepts a Clojure map as its argument. Called when 
                  the route changes with the details of the new route."
  [dispatch-fn]
  (router/start! dispatch-fn))

(def >initialise-db events/>initialise-db)
(def >set-active-page events/>set-active-page)
(def <active-page subs/<active-page)
