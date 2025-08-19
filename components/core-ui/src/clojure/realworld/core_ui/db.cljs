(ns clojure.realworld.core-ui.db
  (:require [clojure.realworld.spec.interface :as user-spec]
            [clojure.spec.alpha :as s]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/db.cljs

;; -- Local Storage ----------------------------------------------------------
;;
(def conduit-user "conduit-user")

(defn set-user-ls
  "Store user in localStorage"
  [user]
  (when user
    (.setItem js/localStorage conduit-user (js/JSON.stringify (clj->js user)))))

(defn remove-user-ls
  "Remove user from localStorage"
  []
  (.removeItem js/localStorage conduit-user))

(defn get-user-ls
  "Get user from localStorage"
  []
  (when-let [user (.getItem js/localStorage conduit-user)]
    (js->clj (js/JSON.parse user) :keywordize-keys true)))

;; -- Re-frame Coeffects -----------------------------------------------------
;;
(defn >local-store-user
  "Get user from localStorage and put into coeffects"
  [coeffects _event]
  (assoc coeffects :local-store-user (get-user-ls)))

;; -- Database ---------------------------------------------------------------
;;
(def default-db
  {:active-page :home
   :active-article nil
   :articles {}
   :articles-count 0
   :comments {}
   :errors {}
   :loading {}
   :profile {}
   :tags []
   :user (get-user-ls)})

;; -- Specs ------------------------------------------------------------------
;;
(s/def ::active-page keyword?)
(s/def ::active-article (s/nilable string?))
(s/def ::articles map?)
(s/def ::articles-count number?)
(s/def ::comments map?)
(s/def ::errors map?)
(s/def ::loading map?)
(s/def ::profile map?)
(s/def ::tags vector?)
(s/def ::user (s/nilable ::user-spec/user))

(s/def ::db (s/keys :req-un [::active-page
                             ::active-article
                             ::articles
                             ::articles-count
                             ::comments
                             ::errors
                             ::loading
                             ::profile
                             ::tags
                             ::user]))
