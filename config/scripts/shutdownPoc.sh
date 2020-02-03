#!/bin/bash

echo ""
echo Deteniendo poc-cc
ps -fu $(whoami) | grep "java -jar poc-" | grep -v grep
ps -fu $(whoami) | grep "java -jar poc-" | grep -v grep | awk '{ print "kill -SIGTERM " $2 }' | /bin/bash
sleep 2
echo ""

KPID=$(ps -fu $(whoami) | grep "kafka.Kafka" | grep -v grep | awk '{ print $2 }')
if [ $KPID ] ; then
  echo Deteniendo Kafka PID $KPID
  /home/poc/kafka/bin/kafka-server-stop.sh
  sleep 5
fi

ZPID=$(ps -fu $(whoami) | grep QuorumPeerMain | grep -v grep | awk '{ print $2 }')
if [ $ZPID ] ; then
  echo Deteniendo ZooKeeper PID $ZPID
  /home/poc/kafka/bin/zookeeper-server-stop.sh
  sleep 2
  echo ""
fi

#ps -fu $(whoami) | grep java | grep -v elk | grep -v grep

echo Limpiando espacio de trabajo
cd $HOME
rm -rf workspace
rm -rf .m2/repository/org/chusete81
rm -rf /tmp/tomcat.*
rm -rf /tmp/kafka-logs
rm -rf /tmp/zookeeper
rm -rf /home/poc/kafka/logs/*
rm -rf /home/poc/kafka/nohup-*.out
echo ""
