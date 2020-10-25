(ns clojure.realworld.rest-api.main
  (:require [clojure.realworld.rest-api.api :as api]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (api/init)
  (run-jetty api/app {:port (Integer/valueOf (or (System/getenv "port") "6003") 10)}))
