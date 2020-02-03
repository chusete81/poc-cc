#!/bin/bash

echo ""
echo "Descargando Apache Kafka..."
cd /opt
curl http://apache.rediris.es/kafka/2.3.1/kafka_2.12-2.3.1.tgz | tar -xz
mv kafka_2.12-2.3.1 kafka

mv /tmp/server.properties $KAFKA_HOME/config
mv /tmp/startKafka.sh $KAFKA_HOME/bin
rm -rf /tmp/* 2> /dev/null
echo ""