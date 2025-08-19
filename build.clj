(ns build
  "The build script for the Polylith project.
  Primary targets:
  * uberjar :project PROJECT
    - creates an uberjar for the given project
  For help, run:
  clojure -A:deps -T:build help/doc"
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.java.shell :as shell]
   [clojure.java.process]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [clojure.tools.deps :as t]
   [clojure.tools.deps.util.dir :refer [with-dir]]
   [org.corfield.build :as bb]))

(defn- get-project-aliases []
  (let [edn-fn (juxt :root-edn :project-edn)]
    (-> (t/find-edn-maps)
        (edn-fn)
        (t/merge-edns)
        :aliases)))

(defn- ensure-project-root
  "Given a task name and a project name, ensure the project
  exists and seems valid, and return the absolute path to it."
  [task project]
  (let [project-root (str (System/getProperty "user.dir") "/projects/" project)]
    (when-not (and project
                   (.exists (io/file project-root))
                   (.exists (io/file (str project-root "/deps.edn"))))
      (throw (ex-info (str task " task requires a valid :project option") {:project project})))
    project-root))

(defn uberjar
  "Builds an uberjar for the specified project.
  Options:
  * :project - required, the name of the project to build,
  * :uber-file - optional, the path of the JAR file to build,
    relative to the project folder; can also be specified in
    the :uberjar alias in the project's deps.edn file; will
    default to target/PROJECT.jar if not specified.
  Returns:
  * the input opts with :class-dir, :compile-opts, :main, and :uber-file
    computed.
  The project's deps.edn file must contain an :uberjar alias
  which must contain at least :main, specifying the main ns
  (to compile and to invoke)."
  [{:keys [project uber-file] :as opts}]
  (let [project-root (ensure-project-root "uberjar" project)
        aliases      (with-dir (io/file project-root) (get-project-aliases))
        main         (-> aliases :uberjar :main)]
    (when-not main
      (throw (ex-info (str "the " project " project's deps.edn file does not specify the :main namespace in its :uberjar alias")
                      {:aliases aliases})))
    (binding [b/*project-root* project-root]
      (let [class-dir "target/classes"
            uber-file (or uber-file
                          (-> aliases :uberjar :uber-file)
                          (str "target/" project ".jar"))
            opts      (merge opts
                             {:class-dir    class-dir
                              :compile-opts {:direct-linking true}
                              :main         main
                              :uber-file    uber-file})]
        (b/delete {:path class-dir})
        (bb/uber opts)
        (b/delete {:path class-dir})
        (println "Uberjar is built.")
        opts))))

(defn- ensure-workspace-name 
  "Ensures the project has a valid package.json and returns the value 
   of the name attribute from the package.json. 
   
   - For build-app task, The package.json must also have a script named \"build\".
   - For test-ci-app task, The package.json must also have a script named \"test:ci\"."
  [task project]
  (let [project-root (str (System/getProperty "user.dir") "/projects/" project)]
    (when-not (and project
                   (.exists (io/file project-root))
                   (.exists (io/file (str project-root "/deps.edn")))
                   (.exists (io/file (str project-root "/package.json"))))
      (throw (ex-info (str task " task requires a valid :project option") {:project project})))
    (let [package-json-str (slurp (io/file project-root "package.json"))
          {:strs [name scripts]} (json/read-str package-json-str)
          has-script? (or (and (= "build-app" task) (contains? scripts "build"))
                          (and (= "test-ci-app" task) (contains? scripts "test:ci")))]
      (when-not name
        (throw (ex-info (str task " task requires a valid yarn workspace") {:project project})))
      (when-not has-script?
        (throw (ex-info (str task " task requires a valid script in package.json")
                        {:project project})))
      name)))

(defn run-sh 
  "Runs the given shell script. 
   
   Prints the result of the script. Exits process if failed. Returns nil if success."
  [& args]
  (println "Running:" (str/join " " args))
  (let [{:keys [exit out err]} (apply shell/sh args)]
    (if (= 0 exit)
      (do (println out)
          nil)
      (do (println err)
          (System/exit exit)))))

(defn build-app 
  "Builds a frontend application for the specified project.
   
   Options:
   * :project - required, the name of the project to build
   
   To run this command:
   - The project must contain a valid package.json file.
   - The package.json file should define a name and the project 
     must be part of the yarn workspaces.
   - The package.json file must have a script named \"build\"."
  [{:keys [project]}]
  (let [workspace-name (ensure-workspace-name "build-app" project)]
    (run-sh "yarn")
    (run-sh "yarn" "workspace" workspace-name "build")))

(defn test-ci-app 
  "Tests a frontend application for the specified project on the CI.
   
   Options:
   * :project - required, the name of the project to build
   
   To run this command:
   - The project must contain a valid package.json file.
   - The package.json file should define a name and the project 
     must be part of the yarn workspaces.
   - The package.json file must have a script named \"test:ci\"."
  [{:keys [project]}]
  (if (= "development" (str project))
    (do
      (run-sh "yarn")
      (run-sh "yarn" "test:ci"))
    (let [workspace-name (ensure-workspace-name "test-ci-app" project)]
      (run-sh "yarn")
      (run-sh "yarn" "workspace" workspace-name "test:ci"))))
