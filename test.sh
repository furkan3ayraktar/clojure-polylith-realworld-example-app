#!/usr/bin/env bash
set -e

clj -A:env/build-tools -m clojure.realworld.build-tools.core interface-check

if [[ $? -eq 0 ]]
then
  clj -A:env/build-tools:service/realworld-backend:service.test/realworld-backend -m clojure.realworld.build-tools.core run-tests realworld-backend
fi
