(defproject clojure.org.realworld/database "0.1"
  :description "database component"
  :dependencies [[clojure.org.realworld/interfaces "1.0"]
                 [environ "1.1.0"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :aot :all)
