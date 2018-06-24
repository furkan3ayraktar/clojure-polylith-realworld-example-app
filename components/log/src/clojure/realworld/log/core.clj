(ns clojure.realworld.log.core
  (:require [taoensso.timbre :as timbre]))

(defmacro info [args]
  `(timbre/log! :info :p ~args))

(defmacro warn [args]
  `(timbre/log! :warn :p ~args))

(defmacro error [args]
  `(timbre/log! :error :p ~args))
