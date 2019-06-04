(ns clojure.realworld.build-tools.ring.server
  (:require [ring.server.standalone :as ring-server]))

(defn- load-var [sym]
  (when sym
    (require (-> sym namespace symbol))
    (find-var sym)))

(defn serve [deps service-name]
  (let [handler (load-var (-> deps :ring :handler))]
    (when-not handler
      (throw (ex-info "You need to provide a handler in your ring configuration." {:err :invalid-ring-config})))
    (ring-server/serve
      handler
      (merge
        {:join? true}
        (:ring deps)
        {:init                  (load-var (-> deps :ring :init))
         :destroy               (load-var (-> deps :ring :destroy))
         :stacktrace-middleware (load-var (-> deps :ring :stacktrace-middleware))
         :reload-paths          (into (or (-> deps :ring :reload-paths) [])
                                      (-> deps :aliases (get (keyword "service" service-name)) :extra-paths))})))) ;; TODO: instead of using service-name and deps, use classpath and top dir to determine source files?
