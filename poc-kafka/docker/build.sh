#!/bin/bash

APP_NAME=poc-kafka

echo ""
docker build -t org.chusete81/${APP_NAME}:latest .
echo ""
docker history org.chusete81/${APP_NAME}:latest
echo ""
docker images
echo ""