(ns clojure.realworld.rest-api.api
  (:require [clojure.realworld.database.interface :as database])
  (:require [clojure.realworld.rest-api.handler :as h]
            [clojure.realworld.rest-api.middleware :as m]
            [clojure.realworld.log.interface :as log]
            [compojure.core :refer [routes wrap-routes defroutes GET POST PUT DELETE ANY OPTIONS]]
            [ring.logger.timbre :as logger]
            [ring.middleware.json :as js]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.multipart-params :as mp]
            [ring.middleware.nested-params :as np]
            [ring.middleware.params :as pr]))

(defroutes public-routes
  (OPTIONS "/**"                              [] h/options)
  (GET     "/api/health"                      [] h/health)
  (POST    "/api/users/login"                 [] h/login)
  (POST    "/api/users"                       [] h/register)
  (GET     "/api/profiles/:username"          [] h/profile)
  (GET     "/api/articles"                    [] h/articles)
  (GET     "/api/articles/:slug"              [] h/article)
  (GET     "/api/articles/:slug/comments"     [] h/comments)
  (GET     "/api/tags"                        [] h/tags))

(defroutes private-routes
  (GET     "/api/user"                        [] h/current-user)
  (PUT     "/api/user"                        [] h/update-user)
  (POST    "/api/profiles/:username/follow"   [] h/follow-profile)
  (DELETE  "/api/profiles/:username/follow"   [] h/unfollow-profile)
  (GET     "/api/articles/feed"               [] h/feed)
  (POST    "/api/articles"                    [] h/create-article)
  (PUT     "/api/articles/:slug"              [] h/update-article)
  (DELETE  "/api/articles/:slug"              [] h/delete-article)
  (POST    "/api/articles/:slug/comments"     [] h/add-comment)
  (DELETE  "/api/articles/:slug/comments/:id" [] h/delete-comment)
  (POST    "/api/articles/:slug/favorite"     [] h/favorite-article)
  (DELETE  "/api/articles/:slug/favorite"     [] h/unfavorite-article))

(defroutes other-routes
  (ANY     "/**"                              [] h/other))

(def ^:private app-routes
  (routes
    (-> private-routes
        (wrap-routes m/wrap-authorization)
        (wrap-routes m/wrap-auth-user))
    (-> public-routes
        (wrap-routes m/wrap-auth-user))
    other-routes))

(def app
  (-> app-routes
      logger/wrap-with-logger
      kp/wrap-keyword-params
      pr/wrap-params
      mp/wrap-multipart-params
      js/wrap-json-params
      np/wrap-nested-params
      m/wrap-exceptions
      js/wrap-json-response
      m/wrap-cors))

(defn init []
  (try
    (log/init)
    (let [db (database/db)]
      (if (database/db-exists?)
        (if (database/valid-schema? db)
          (log/info "Database schema is valid.")
          (do
            (log/warn "Please fix database schema and restart")
            (System/exit 1)))
        (do
          (log/info "Generating database.")
          (database/generate-db db)
          (log/info "Database generated."))))
    (log/info "Initialized server.")
    (catch Exception e
      (log/error e "Could not start server."))))

(defn destroy []
  (log/info "Destroyed server."))
