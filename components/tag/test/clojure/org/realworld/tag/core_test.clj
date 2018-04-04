(ns clojure.org.realworld.tag.core-test
  (:require [clojure.test :refer :all]
            [clojure.org.realworld.database.interface :as database]
            [clojure.org.realworld.tag.core :as core]
            [clojure.java.jdbc :as jdbc]))

(defn test-db
  ([] {:classname   "org.sqlite.JDBC"
       :subprotocol "sqlite"
       :subname     "test.db"})
  ([_] (test-db)))

(defn prepare-for-tests [f]
  (with-redefs [database/db test-db]
    (let [db (test-db)]
      (database/generate-db db)
      (f)
      (database/drop-db db))))

(use-fixtures :each prepare-for-tests)

(deftest all-tags--return-tags-response
  (let [_ (jdbc/insert-multi! (test-db) :tag [{:name "tag1"}
                                              {:name "tag2"}
                                              {:name "tag3"}
                                              {:name "tag4"}
                                              {:name "tag5"}])
        [ok? res] (core/all-tags)]
    (is (true? ok?))
    (is (= {:tags ["tag1" "tag2" "tag3" "tag4" "tag5"]} res))))
