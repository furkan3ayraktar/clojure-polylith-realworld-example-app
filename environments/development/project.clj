(defproject clojure.org.realworld/development "1.0"
  :description "The main development environment"
  :profiles {:dev {:plugins      [[lein-environ "1.1.0"]
                                  [lein-ring "0.9.7"]]
                   :dependencies [[ring/ring-mock "0.3.0"]]
                   :test-paths   ["test"]
                   :env          {:allowed-origins "http://localhost:3000"
                                  :environment     "LOCAL"}}}
  :dependencies [[clj-jwt "0.1.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [compojure "1.6.0"]
                 [crypto-password "0.2.0"]
                 [environ "1.1.0"]
                 [honeysql "0.9.2"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.clojure/test.check "0.9.0"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-json "0.5.0-beta1"]
                 [ring-logger-timbre "0.7.6"]
                 [slugger "1.0.1"]]
  :ring {:init    clojure.org.realworld.backend.api/init
         :destroy clojure.org.realworld.backend.api/destroy
         :handler clojure.org.realworld.backend.api/app
         :port    6003})
