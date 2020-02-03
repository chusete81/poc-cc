#!/bin/bash

APP_NAME=poc-connectors
APP_PORT=8900
POC_CC_KAFKA_URL=172.17.0.1:29000
POC_CC_CONN_BACKEND_URL=http://172.17.0.1:9000/invoke

echo ""
docker run -d --name ${APP_NAME} -p ${APP_PORT}:${APP_PORT} -e "POC_CC_KAFKA_URL=${POC_CC_KAFKA_URL}" -e "POC_CC_CONN_BACKEND_URL=${POC_CC_CONN_BACKEND_URL}" org.chusete81/${APP_NAME}:latest
echo ""
docker ps
echo ""