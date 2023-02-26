## Steps required to use IntelliJ IDEA / Cursive
If you use Cursive as an IDE, the default configuration using `:local/root` in the root `deps.edn` file for the local development project will not work. The problem is that Cursive doesn't treat local dependencies as source files. There is an [open issue](https://github.com/cursive-ide/cursive/issues/2554) in Cursive repository.

In many other IDE's like VSCode/Calva and Emacs/Cider this works fine, which also gives us some benefits:
- Less code, one line instead of two.
- It's more consistent with how projects are specified.
- You can add or remove the resources directory from a brick, without updating the root `deps.edn`.

To make Cursive recognize source, resource, and test directories and dependencies for the development project, we need to alter the root `deps.edn` file. This requires changes to the `:dev` alias. By default, the `:dev` alias looks like this:
```clojure
{:extra-paths ["development/src"]

 :extra-deps  {; Components
               poly/article {:local/root "components/article"}
               poly/comment {:local/root "components/comment"}
               poly/database {:local/root "components/database"}
               poly/env {:local/root "components/env"}
               poly/log {:local/root "components/log"}
               poly/profile {:local/root "components/profile"}
               poly/spec {:local/root "components/spec"}
               poly/tag {:local/root "components/tag"}
               poly/user {:local/root "components/user"}

               ; Bases
               poly/rest-api {:local/root "bases/rest-api"}

               ; Development dependencies 
               djblue/portal {:mvn/version "0.35.1"}
               org.clojure/clojure {:mvn/version "1.11.1"}
               org.slf4j/slf4j-nop {:mvn/version "2.0.3"}}}
```

For Cursive, change the `:dev` alias to have the following content:
```clojure
{:extra-paths ["development/src"

               ; Components
               "components/article/resources"
               "components/article/src"
               "components/comment/resources"
               "components/comment/src"
               "components/database/resources"
               "components/database/src"
               "components/env/resources"
               "components/env/src"
               "components/log/resources"
               "components/log/src"
               "components/profile/resources"
               "components/profile/src"
               "components/spec/resources"
               "components/spec/src"
               "components/tag/resources"
               "components/tag/src"
               "components/user/resources"
               "components/user/src"

               ; Bases
               "bases/rest-api/resources"
               "bases/rest-api/src"]

 :extra-deps  {clj-jwt/clj-jwt {:mvn/version "0.1.1"}
               clj-time/clj-time {:mvn/version "0.15.2 "}
               com.github.seancorfield/honeysql {:mvn/version "2.4.980"}
               com.taoensso/timbre {:mvn/version "6.0.4"}
               compojure/compojure {:mvn/version "1.7.0"}
               crypto-password/crypto-password {:mvn/version "0.3.0"}
               metosin/spec-tools {:mvn/version "0.10.5"}
               org.clojure/data.json {:mvn/version "2.4.0"}
               org.clojure/java.jdbc {:mvn/version "0.7.12"}
               org.clojure/test.check {:mvn/version "1.1.1"}
               org.xerial/sqlite-jdbc {:mvn/version "3.41.0.0"}
               ring-logger-timbre/ring-logger-timbre {:mvn/version "0.7.6"}
               ring/ring-jetty-adapter {:mvn/version "1.9.6"}
               ring/ring-json {:mvn/version "0.5.1"}
               slugger/slugger {:mvn/version "1.0.1"}

               ; Development dependencies 
               djblue/portal {:mvn/version "0.35.1"}
               org.clojure/clojure {:mvn/version "1.11.1"}
               org.slf4j/slf4j-nop {:mvn/version "2.0.3"}}}
```

> Do not forget to update `:extra-paths` and `:extra-deps` when you add, update, or delete a component or change its dependencies.
