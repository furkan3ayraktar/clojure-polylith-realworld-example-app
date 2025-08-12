(ns clojure.realworld.web-ui.events-test
  (:require [cljs.test :refer [deftest is]]
            [clojure.realworld.core-ui.events :as events]))

(deftest endpoint--construct-full-endpoint--return-url
  (is (= "http://localhost:6003/api/articles/a-slug"
         (events/endpoint "articles" "a-slug"))))
