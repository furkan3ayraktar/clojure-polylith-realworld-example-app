(ns dev.getting-started
  (:require [clj-http.client :as http]))

;; Welcome to the REPL for Getting Started with Polylith Real World example!
;;
;; If you are unfamiliar with Calva or Clojure, please open the file
;; development/src/dev/hello_repl.clj

;; To confirm that you know Calva well enough to continue this session,
;; 1. Load this file in the REPL
;; 2. Evaluate this string:

"Hello Polylith Real World!"

;; Great! Let's continue.
;; The guide is designed to run in Rich Comment Forms like the one below
;; You are supposed to read instructions and evaluate the top level forms
;; in the RCF (in that order 😀). If there is no instruction to evaluate
;; a top level form, consider it an implied instruction to do so.
;; Please don't hesitate to experiment with the code!

(comment
  ;; The server is running, we should be able to grab some articles

  ;; This is the API base URL
  (def base-url "http://localhost:6003/api")

  ;; Fetch some articles
  (def articles (http/get (str base-url "/articles") {:as :auto}))
  ;; Look at them
  (:body articles)

  ;; Oh, no articles? We need to add one!

  ;; First we need to create a user, this payload.
  (def register-payload {:user {:username "Polylith Explorer"
                                :email "polylith-is-cool@example.com"
                                :password "battery-staple-horse"}})

  ;; Creating the user
  ;; We hold on to the token so that we can create articles later
  (def jwt-token (let [register-response (http/post (str base-url "/users")
                                                    {:form-params register-payload
                                                     :content-type :json
                                                     :as :auto})]
                   (->> register-response :body :user :token)))
  ;; Consider evaluating `jwt-token` above to “save” the token in the
  ;; repl/output window.

  ;; Now our first article
  (def article-payload {:article {:title "Polylith Real World example article"
                                  :description "My first article. And it is about Polylith of course!"
                                  :body "Yada, yada, yada. Too lazy right now"
                                  :tagList ["polylith" "clojure" "realworld"]}})

  (def article-request-headers {"Authorization" (str "Token " jwt-token)})

  (http/post (str base-url "/articles")
             {:form-params article-payload
              :headers article-request-headers
              :content-type :json})

  ;; We define the request so that we can examine it later
  (def article-request *1)


  (def articles-again (http/get (str base-url "/articles") {:as :auto}))
  (:body articles-again)
  (->> articles-again :body :articlesCount)
  


  :rcf)

;; ======== The poly tool ========
;; Learn about the poly tool here: https://polylith.gitbook.io/poly/
;; We have started the tool in a terminal, open the terminal pane
;; and you should find a terminal named “Poly tool”
;; Commands you can try
;; * `check` - checks the integrety of the project
;; * `help`
;; * `info`
;; * `help info`
;; * `test :all`
;; * `help create

(comment

  ;; TODO: @furkan3ayraktar and @tengstrand and @Misophistful, please fill in the blank space!

  :rcf)


"Hello dear Polylith Real World explorer! You loaded this file in the REPL."