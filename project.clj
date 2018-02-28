(defproject clojure.org.realworld/development "1.0"
  :description "The workspace"
  :plugins [[polylith/lein-polylith "0.0.35-alpha"]]
  :polylith {:vcs "git"
             :build-tool "leiningen"
             :top-ns "clojure.org.realworld"
             :top-dir "clojure/org/realworld"
             :ignored-tests []
             :clojure-version "1.9.0"
             :clojure-spec-version "org.clojure/spec.alpha 0.1.143"
             :example-hash1 ""
             :example-hash2 ""})
