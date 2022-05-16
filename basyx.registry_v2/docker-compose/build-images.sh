#!/bin/sh
cd ..
mvn clean install -f aas-registry-plugins
mvn clean install -DskipTests -Ddocker.username=aas-registry-test

