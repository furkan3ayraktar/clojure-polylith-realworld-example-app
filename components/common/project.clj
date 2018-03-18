(defproject clojure.org.realworld/common "0.1"
  :description "A common component"
  :dependencies [[clojure.org.realworld/interfaces "1.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [environ "1.1.0"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/test.check "0.9.0"]]
  :aot :all)
