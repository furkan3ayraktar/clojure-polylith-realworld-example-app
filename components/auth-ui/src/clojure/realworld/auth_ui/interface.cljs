(ns clojure.realworld.auth-ui.interface
  (:require [clojure.realworld.auth-ui.views :as views]))

;; -- Authentication UI Components -------------------------------------------
;;
(defn login
  "Render the login page"
  []
  [views/login])

(defn register
  "Render the registration page"
  []
  [views/register])

(defn settings
  "Render the user settings page"
  []
  [views/settings]) 
