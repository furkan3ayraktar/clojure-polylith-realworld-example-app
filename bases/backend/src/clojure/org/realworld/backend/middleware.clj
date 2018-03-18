(ns clojure.org.realworld.backend.middleware
  (:require [clojure.string :as str]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]))

(defn wrap-auth-token [handler]
  (fn [req]
    (let [authorization (get (:headers req) "authorization")
          token (-> (str/split authorization #" ") last)]
      (if (str/blank? token)
        {:status 401
         :body {:errors {:authorization "Authorization required."}}}
        (handler (assoc req :auth-token token))))))

(defn wrap-exceptions [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (let [message  (str "An unknown exception occurred.")]
          (log/error e message)
          {:status 500
           :body   {:errors {:other [message]}}})))))

(defn create-access-control-header [origin]
  (let [origins        (str/split (env :allowed-origins) #",")
        allowed-origin (some #{origin} origins)]
    {"Access-Control-Allow-Origin"      allowed-origin
     "Access-Control-Allow-Methods"     "POST, GET, PUT, OPTIONS, DELETE"
     "Access-Control-Max-Age"           "3600"
     "Access-Control-Allow-Headers"     "Content-Type, x-requested-with"
     "Access-Control-Allow-Credentials" "true"}))

(defn wrap-cors [handler]
  (fn [req]
    (let [origin   (get (:headers req) "origin")
          response (handler req)]
      (assoc response :headers (merge (:headers response) (create-access-control-header origin))))))