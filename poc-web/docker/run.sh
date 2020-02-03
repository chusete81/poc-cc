#!/bin/bash

APP_NAME=poc-web
APP_PORT=8080
POC_CC_HUB_ENDPOINT=http://172.17.0.1:8888

echo ""
docker run -d --name ${APP_NAME} -p ${APP_PORT}:${APP_PORT} -e "POC_CC_HUB_ENDPOINT=${POC_CC_HUB_ENDPOINT}" org.chusete81/${APP_NAME}:latest
echo ""
docker ps
echo ""