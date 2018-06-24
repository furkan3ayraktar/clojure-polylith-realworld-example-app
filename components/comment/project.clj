(defproject clojure.realworld/comment "0.1"
  :description "comment component"
  :dependencies [[clj-time "0.14.2"]
                 [clojure.realworld/interfaces "1.0"]
                 [honeysql "0.9.2"]
                 [metosin/spec-tools "0.6.1"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :aot :all)
