#!/usr/bin/env zsh

echo "-> Running linter..."

docker run -v $PWD/:/clojure-polylith-realworld-example-app --workdir /clojure-polylith-realworld-example-app --rm cljkondo/clj-kondo clj-kondo --lint bases components development --config '{:output {:pattern "::{{level}} file={{filename}},line={{row}},col={{col}}::{{message}}"}}'

echo "-> Linting completed."
