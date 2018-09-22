(defproject clojure.org.realworld/tag "0.1"
  :description "A tag component"
  :dependencies [[honeysql "0.9.2"]
                 [clojure.realworld/interfaces "1.0"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :aot :all)
