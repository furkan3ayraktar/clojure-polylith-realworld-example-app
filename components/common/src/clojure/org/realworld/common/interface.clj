(ns clojure.org.realworld.common.interface
  (:require [clojure.org.realworld.common.core :as core]
            [clojure.org.realworld.common.logging :as logging]
            [clojure.org.realworld.common.spec]))

(defn init-logging []
  (logging/init))
