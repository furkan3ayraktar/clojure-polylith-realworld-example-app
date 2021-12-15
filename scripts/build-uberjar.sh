#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: PROJ_NAME, e.g.: realworld-backend"
    exit 1
fi

cd projects/$1

mkdir -p classes
mkdir -p target

rm -rf classes/*
rm -rf target/$1.*

echo "Compiling project..."

clojure -A:aot

if [[ $? -ne 0 ]]
then
  echo "Could not compile project."
  exit 1
fi

echo "Project compiled. Creating an uberjar for the project..."

clojure -A:uberjar

if [[ $? -ne 0 ]]
then
  echo "Could not create uberjar for the projet."
  exit 1
fi

echo "Uberjar created."
