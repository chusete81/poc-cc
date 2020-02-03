#!/bin/bash

APP_NAME=poc-back
APP_PORT=9000

echo ""
docker run -d --name ${APP_NAME} -p ${APP_PORT}:${APP_PORT} org.chusete81/${APP_NAME}:latest
echo ""
docker ps
echo ""