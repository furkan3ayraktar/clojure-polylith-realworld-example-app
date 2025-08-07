(ns clojure.realworld.web-ui.interface
  (:require [clojure.realworld.web-ui.views :as views]
            [clojure.realworld.core-ui.interface :as core-ui]))

(defn app
  "Renders the application. The router will route to
   correct page through this entry point."
  []
  [views/app])

;; Delegate to core-ui for all core functionality
(def >initialise-db core-ui/>initialise-db)
(def >set-active-page core-ui/>set-active-page)
(def <active-page core-ui/<active-page)
(def start-router! core-ui/start-router!)
(def url-for core-ui/url-for)
