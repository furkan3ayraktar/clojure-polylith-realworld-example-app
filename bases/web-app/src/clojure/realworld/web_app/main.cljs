(ns clojure.realworld.web-app.main
  (:require [clojure.realworld.web-ui.interface :as web-ui]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [reagent.dom :as rdom]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/core.cljs

(defn ^:export init
  "This is the entry point to the application. This functiomn is called when the page is loaded."
  []
  ;; Here we are just hooking up the router on app start
  (web-ui/start-router! #(dispatch [web-ui/>set-active-page %]))

  ;; Put an initial value into app-db. Using the sync version 
  ;; of dispatch means that value is in place before we go 
  ;; onto the next step.
  (dispatch-sync [web-ui/>initialise-db])

  ;; Render the UI into the HTML's <div id="app" /> element
  (rdom/render [web-ui/app]
    (.getElementById js/document "app")))
