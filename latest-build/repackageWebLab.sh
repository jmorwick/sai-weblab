#!/bin/bash

cd

cd Documents/GitHub/sai
mvn clean
mvn package
mvn install

cd ..
cd sai-weblab
mvn clean
mvn package
mvn package spring-boot:repackage

cd target
cp -f sai-weblab-0.0.1-SNAPSHOT.jar ~/Documents/GitHub/sai-weblab/latest-build/sai-weblab-0.0.1-SNAPSHOT.jar
