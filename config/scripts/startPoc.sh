#!/bin/bash

export POC_CC_GLOBAL_USER_TIMEOUT=60000
export POC_CC_CONN_CONSUMER_THREADS=96
export POC_CC_KAFKA_URL=localhost:9092
#export POC_CC_BACK_ERROR_PERC=1

VER_BACK=2.0.0
VER_CONN=2.0.0
VER_HUB=2.0.0
VER_WEB=2.0.0

cd $HOME
./shutdownPoc.sh
WORKDIR=workspace
mkdir $WORKDIR
cd $HOME/$WORKDIR
mkdir target

echo Descargando codigo fuente
git clone -v https://chusete81@github.com/chusete81/kafka-connector.git
echo ""
git clone -v -b upgrade https://chusete81@github.com/chusete81/poc-cc.git
echo ""

echo Iniciando ZooKeeper
cd $HOME/kafka
./zooStart.sh
sleep 3
echo ""

echo Iniciando Kafka
cp $HOME/$WORKDIR/poc-cc/poc-kafka/config/*.properties $HOME/kafka/config
cd $HOME/kafka
./startKafka.sh
sleep 1
echo ""

echo Compilando
cd $HOME/$WORKDIR/kafka-connector/kafka-connector
mvn install -DskipTests -T 1C

cd $HOME/$WORKDIR/poc-cc
mvn package -DskipTests -T 1C
echo ""

echo Artefactos generados:
cd $HOME/$WORKDIR/target
pwd
echo ""
mv $HOME/$WORKDIR/poc-cc/poc-back/target/poc-back-$VER_BACK.jar ./poc-back.jar
mv $HOME/$WORKDIR/poc-cc/poc-connectors/target/poc-connectors-$VER_CONN.jar ./poc-connectors.jar
mv $HOME/$WORKDIR/poc-cc/poc-hub/target/poc-hub-$VER_HUB.jar ./poc-hub.jar
mv $HOME/$WORKDIR/poc-cc/poc-web/target/poc-web-$VER_WEB.jar ./poc-web.jar
ls -l            
echo ""
rm -rf $HOME/.m2/repository/org/chusete81/poc-cc

echo Inicializando topics
$HOME/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions $POC_CC_CONN_CONSUMER_THREADS --topic pocRequestsQueue
$HOME/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 16 --topic pocResponsesQueue
echo ""

echo Levantando servicios
mkdir logs
nohup java -jar poc-connectors.jar > ./logs/poc-connectors.log &
sleep 4
nohup java -jar poc-web.jar > ./logs/poc-web.log &
sleep 2
nohup java -jar poc-hub.jar > ./logs/poc-hub.log &
sleep 2
nohup java -jar poc-back.jar > ./logs/poc-back.log &
sleep 1

echo ""
ps -fu $(whoami) | grep java | grep poc- | grep -v grep
echo ""

echo curl http://localhost:8080/
while [[ $(curl -v http://localhost:8080 2> /dev/stdout > /dev/null | grep -c "HTTP/1.1 200") -lt 1 ]] ; do
  echo ""
  sleep 2
done

curl http://localhost:8080/
echo ""
echo ""


###
waitForURL() {
  if [ $URL ] ; then
    while [[ $(curl -v $URL 2> /dev/stdout > /dev/null | grep -c "HTTP/1.1 200") -lt 1 ]] ; do
      sleep 1
    done
	curl $URL
  fi
}
#URL=http://localhost:9000
#waitForURL
