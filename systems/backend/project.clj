(defproject clojure.org.realworld/backend "0.1"
  :description "A backend system"
  :profiles {:dev {:plugins      [[lein-environ "1.1.0"]
                                  [lein-ring "0.9.7"]]
                   :dependencies [[ring/ring-mock "0.3.0"]]
                   :test-paths   ["test"]
                   :env          {:allowed-origins "http://localhost:3000"
                                  :environment     "LOCAL"}}}
  :dependencies [[clj-jwt "0.1.1"]
                 [clj-time "0.14.2"]
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
  :ring {:init    clojure.org.realworld.backend.core/init
         :destroy clojure.org.realworld.backend.core/destroy
         :handler clojure.org.realworld.backend.core/app
         :port    6003}
  :aot :all)
