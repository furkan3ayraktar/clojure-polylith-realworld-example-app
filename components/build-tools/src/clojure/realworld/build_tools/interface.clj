(ns clojure.realworld.build-tools.interface
  (:require [clojure.realworld.build-tools.ring.server :as ring-server]))

(defn serve [deps]
  (ring-server/serve deps))
