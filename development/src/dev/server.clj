(ns dev.server
  (:require [clojure.realworld.rest-api.main :as main]))

(defn start! [port]
  (main/start! port))

(defn stop! []
  (main/stop!))

(comment
  (start! 6003)
  (stop!)
  
  )
