#!/usr/bin/env bash

cd jdbl-pom
mvn clean install -Ptravis
mvn jacoco:report coveralls:report --fail-never