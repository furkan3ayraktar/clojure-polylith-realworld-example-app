(ns clojure.realworld.profile.spec
  (:require [clojure.realworld.spec.interface :as spec]
            [spec-tools.data-spec :as ds]))

(def profile
  (ds/spec {:name :core/profile
            :spec {:username       spec/username?
                   :following      boolean?
                   (ds/opt :image) (ds/maybe spec/uri-string?)
                   (ds/opt :bio)   (ds/maybe spec/non-empty-string?)}}))
