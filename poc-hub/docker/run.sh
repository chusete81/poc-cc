#!/bin/bash

APP_NAME=poc-hub
APP_PORT=8888
POC_CC_KAFKA_URL=172.17.0.1:29000

echo ""
docker run -d --name ${APP_NAME} -p ${APP_PORT}:${APP_PORT} -e "POC_CC_KAFKA_URL=${POC_CC_KAFKA_URL}" org.chusete81/${APP_NAME}:latest
echo ""
docker ps
echo ""