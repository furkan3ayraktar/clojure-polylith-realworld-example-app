#!/usr/bin/env bash
set -e

clj -A:env/build-tools:service/realworld-backend -m clojure.realworld.build-tools.core serve realworld-backend
