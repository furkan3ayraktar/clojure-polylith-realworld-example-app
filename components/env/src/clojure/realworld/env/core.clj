(ns clojure.realworld.env.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn keywordize [s]
  (when s
    (-> (str/lower-case s)
        (str/replace "_" "-")
        (str/replace "." "-")
        (keyword))))

(defn read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn read-system-props []
  (->> (System/getProperties)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn slurp-file [f]
  (when-let [f (io/file f)]
    (when (.exists f)
      (slurp f))))

(defn read-env-file [f]
  (when-let [content (slurp-file f)]
    (try
      (let [parsed-content (edn/read-string content)]
        (if (map? parsed-content)
          parsed-content
          {}))
      (catch Exception _
        {}))))

(def env
  (let [env-file     (read-env-file "env.edn")
        system-props (read-system-props)
        system-env   (read-system-env)]
    (merge system-props
           system-env
           env-file)))
