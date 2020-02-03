#!/bin/bash

APP_NAME=poc-connectors

cp ../target/${APP_NAME}-2.0.0.jar ./app.jar
echo ""
docker build -t org.chusete81/${APP_NAME}:latest .
rm app.jar
echo ""
docker images
echo ""