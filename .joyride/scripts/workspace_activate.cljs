(ns workspace-activate
  (:require [joyride.core :as joyride]
            ["vscode" :as vscode]))

(defonce !db (atom {:disposables []}))

;; To make the activation script re-runnable we dispose of
;; event handlers and such that we might have registered
;; in previous runs.
(defn- clear-disposables! []
  (run! (fn [disposable]
          (.dispose disposable))
        (:disposables @!db))
  (swap! !db assoc :disposables []))

;; Pushing the disposables on the extension context's
;; subscriptions will make VS Code dispose of them when the
;; Joyride extension is deactivated.
(defn- push-disposable [disposable]
  (swap! !db update :disposables conj disposable)
  (-> (joyride/extension-context)
      .-subscriptions
      (.push disposable)))

(defn- my-main []
  (println "Hello World, from my-main workspace_activate.cljs script")
  (clear-disposables!)

  (let [terminal (vscode/window.createTerminal
                  #js {:name "Poly tool"})]
    (push-disposable terminal)
    (.show terminal true)
    (.sendText terminal "poly")
    (.sendText terminal "check")))

(when (= (joyride/invoked-script) joyride/*file*)
  (my-main))
