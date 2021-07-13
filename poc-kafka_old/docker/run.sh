#!/bin/bash

KFK_HOST=127.0.0.1
KFK_PORT=29000

echo ""
docker run -d --name poc-kafka -p $KFK_PORT:$KFK_PORT -e KAFKA_LISTENERS=PLAINTEXT://$KFK_HOST:$KFK_PORT org.chusete81/poc-kafka:latest
echo ""
docker ps -a
echo ""