#!/bin/bash

APP_NAME=poc-back

echo ""
echo "Stopping/deleting container"
docker rm -f ${APP_NAME}
echo ""
echo "Deleting image"
docker rmi -f org.chusete81/${APP_NAME}
echo ""
echo "docker images:"
docker images
echo ""