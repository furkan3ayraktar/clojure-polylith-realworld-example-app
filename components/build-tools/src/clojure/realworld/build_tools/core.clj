(ns clojure.realworld.build-tools.core
  (:require [clojure.java.io :as io]
            [clojure.realworld.build-tools.ring.server :as ring-server]
            [clojure.realworld.build-tools.polylith.commands :as polylith]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]))

(defn -main [cmd & [service-name]]
  (let [ws-path (.getAbsolutePath (io/file ""))
        deps    (-> "deps.edn" slurp read-string)
        top-ns  (-> deps :polylith :top-namespace)
        top-dir (str/replace top-ns #"\." "/")]
    (try
      (case cmd
        "interface-check" (polylith/interface-check ws-path)
        "run-tests" (polylith/run-tests-for deps service-name)
        "serve" (ring-server/serve deps service-name)
        (println "Allowed options: interface-check and run-tests"))
      (catch Exception e
        (println (.getMessage e))
        (println (.printStackTrace e))
        (System/exit 1))
      (finally
        (System/exit 0)))))
