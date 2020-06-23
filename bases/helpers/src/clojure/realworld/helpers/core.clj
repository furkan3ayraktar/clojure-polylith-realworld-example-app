(ns clojure.realworld.helpers.core
  (:require [clojure.realworld.build-tools.interface :as build-tools])
  (:gen-class))

(defn -main [cmd & [env-name]]
  (let [deps (-> (str "environments/" env-name "/deps.edn") slurp read-string)]
    (try
      (case cmd
        "serve" (build-tools/serve deps)
        (println "Allowed options: serve"))
      (catch Exception e
        (println (.getMessage e))
        (println (.printStackTrace e))
        (System/exit 1))
      (finally
        (System/exit 0)))))
