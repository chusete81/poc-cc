#!/bin/bash

APP_NAME=poc-connectors
APP_VERSION=2.0.1

cp ../target/${APP_NAME}-${APP_VERSION}.jar ./app.jar
echo ""
docker build -t org.chusete81/${APP_NAME}:latest .
rm app.jar
echo ""
docker images
echo ""