(ns dev.furkan
  (:require [portal.api :as p]))

(def portal-atom (atom nil))

(defn launch-portal []
  (let [portal (p/open (or @portal-atom {:launcher :vs-code}))]
    (reset! portal-atom portal)
    (add-tap #'p/submit)
    portal))

(comment
  (launch-portal)

  )
