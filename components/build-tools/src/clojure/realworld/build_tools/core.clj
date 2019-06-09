(ns clojure.realworld.build-tools.core
  (:require [clojure.realworld.build-tools.ring.server :as ring-server]))

(defn -main [cmd & [service-name]]
  (let [deps (-> "deps.edn" slurp read-string)]
    (try
      (case cmd
        "serve" (ring-server/serve deps service-name)
        (println "Allowed options: interface-check and run-tests"))
      (catch Exception e
        (println (.getMessage e))
        (println (.printStackTrace e))
        (System/exit 1))
      (finally
        (System/exit 0)))))
