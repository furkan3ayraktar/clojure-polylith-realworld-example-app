(ns clojure.org.realworld.log.interface
  (:require [clojure.org.realworld.log.config :as config]
            [clojure.org.realworld.log.core :as core]))

(defn init []
  (config/init))

(defmacro info [& args]
  `(core/info ~args))

(defmacro warn [& args]
  `(core/info ~args))

(defmacro error [& args]
  `(core/error ~args))
