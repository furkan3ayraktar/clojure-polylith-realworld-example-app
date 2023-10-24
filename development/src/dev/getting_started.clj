(ns dev.getting-started
  (:require [clj-http.client :as http]))

;; Welcome to the REPL for Getting Started with Polylith Real World example!
;;
;; If you are unfamiliar with Calva or Clojure, please open the file
;; development/src/dev/getting_started.clj
;; Cmd/Control-clicking this symbol will do it:
'dev.hello-repl

;; To confirm that you know Calva well enough to continue this session,
;; Evaluate this string:

"Hello Polylith Real World!"

;; Great! Let's continue.

(comment
  (def res (http/get "http://localhost:6003/api/articles" {:as :auto}))
  (:body res)

  ;; TODO: add an article via the API
  ;;       will we need to create a user first?
  :rcf)




;; TODO: @furkan3ayraktar and @tengstrand and @Misophistful, please fill in the blank space!

