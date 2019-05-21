#!/usr/bin/env bash

cd jdbl-tracer
mvn clean install -Ptravis
mvn jacoco:report coveralls:report --fail-never