#!/bin/bash

rm -rf /tmp/* 2> /dev/null
cd $KAFKA_HOME/bin

# start zookeeper
nohup ./zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /tmp/zookeeper.out &

# kafka config
if [ "x$KAFKA_LISTENERS" = "x" ]; then
    KAFKA_LISTENERS=PLAINTEXT://:9092
fi
echo "" >> $KAFKA_HOME/config/server.properties
echo "listeners=$KAFKA_LISTENERS" >> $KAFKA_HOME/config/server.properties

# start kafka
./kafka-server-start.sh $KAFKA_HOME/config/server.properties