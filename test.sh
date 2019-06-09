#!/usr/bin/env bash
set -e

clj -A:dev -m polylith.main compile

if [[ $? -eq 0 ]]
then
  clj -A:dev -m polylith.main test realworld-backend
fi
