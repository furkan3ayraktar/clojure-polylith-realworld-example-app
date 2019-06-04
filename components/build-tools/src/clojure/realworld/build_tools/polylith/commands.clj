(ns clojure.realworld.build-tools.polylith.commands
  (:require [clojure.java.shell :as shell]
            [clojure.tools.cli :refer [parse-opts]]
            [cognitect.test-runner :as tr]
            [leiningen.polylith.cmd.shared :as shared]))

(defn interface-check [ws-path]
  (let [all-bases      (shared/all-bases ws-path)
        all-components (shared/all-components ws-path)]
    (doseq [c all-components]
      (let [{:keys [exit out err]} (shell/sh "lein" "compile" :dir (str ws-path "/components/" c))]
        (println "Compiled" c)
        (if-not (= 0 exit)
          (throw (ex-info "Exception during interface check" {:err err :exit-code exit :out out})))))
    (doseq [b all-bases]
      (let [{:keys [exit out err]} (shell/sh "lein" "compile" :dir (str ws-path "/bases/" b))]
        (println "Compiled" b)
        (if-not (= 0 exit)
          (throw (ex-info "Exception during interface check" {:err err :exit-code exit :out out})))))))

(defn run-tests-for [deps service-name]
  (let [test-paths (-> deps :aliases (get (keyword "service.test" service-name)) :extra-paths)]
    (tr/test {:dir test-paths})))
