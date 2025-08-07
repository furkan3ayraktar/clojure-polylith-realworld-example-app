(ns clojure.realworld.core-ui.subs
  (:require [re-frame.core :refer [reg-sub]]))

;; This namespace is a modified version of Jacek Schae's implementation.
;; Source: https://github.com/jacekschae/conduit/blob/ae3c15df1b76d3e0157e32ae24bae52bdb7ea365/src/conduit/subs.cljs

(defn reverse-cmp ;; https://clojure.org/guides/comparators
  "Sort numbers in decreasing order, i.e.: calls compare with the arguments in the opposite order"
  [a b]
  (compare b a))

;; -- Subscriptions ----------------------------------------------------------

(def <active-page ::active-page)
(def <articles ::articles)
(def <articles-count ::articles-count)
(def <active-article ::active-article)
(def <tags ::tags)
(def <comments ::comments)
(def <profile ::profile)
(def <loading ::loading)
(def <filter ::filter)
(def <active-filter ::active-filter)
(def <errors ::errors)
(def <user ::user)

(reg-sub
 <active-page          ;; usage: (subscribe [<active-page])
 (fn [db _]            ;; db is the (map) value stored in the app-db atom
   (let [page (:active-page db)]
        (if (map? page)
             (keyword (:name page))
             page)))) ;; extract a value from the application state

(reg-sub
 <articles                              ;; usage: (subscribe [<articles])
 (fn [db _]                             ;; db is the (map) value stored in the app-db atom
   (->> (:articles db)                  ;; ->> is a thread-last macro - pass articles as last arg of
        (vals)                          ;; vals, just as we would write (vals articles), then pass the result to
        (sort-by :epoch reverse-cmp)))) ;; sort-by epoch in reverse order

(reg-sub
 <articles-count ;; usage: (subscribe [<articles-count])
 (fn [db _]
   (:articles-count db)))

(reg-sub
 <active-article ;; usage (subscribe [<active-article])
 (fn [db _]
   (get-in db [:articles (:active-article db)])))

(reg-sub
 <tags ;; usage: (subscribe [<tags])
 (fn [db _]
   (:tags db)))

(reg-sub
 <comments ;; usage: (subscribe [<comments])
 (fn [db _]
   (->> (:comments db)
        (vals)
        (sort-by :epoch reverse-cmp))))

(reg-sub
 <profile ;; usage: (subscribe [<profile])
 (fn [db _]
   (:profile db)))

(reg-sub
 <loading ;; usage: (subscribe [<loading])
 (fn [db _]
   (:loading db)))

(reg-sub
 <filter ;; usage: (subscribe [<filter])
 (fn [db _]
   (:filter db)))

(reg-sub
 <active-filter ;; usage: (subscribe [<active-filter])
 (fn [db _]
   (:active-filter db)))

(reg-sub
 <errors ;; usage: (subscribe [<errors])
 (fn [db _]
   (:errors db)))

(reg-sub
 <user ;; usage: (subscribe [<user])
 (fn [db _]
   (:user db)))
