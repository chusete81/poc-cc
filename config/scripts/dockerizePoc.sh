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
WORKDIR=workspace-$(date +%y%m%d)-$(date +%H%M%S) #%N)
mkdir $WORKDIR
cd $HOME/$WORKDIR
mkdir target

echo ""
echo Descargando codigo fuente
git clone -v https://chusete81@github.com/chusete81/kafka-connector.git
echo ""
git clone -v -b upgrade https://chusete81@github.com/chusete81/poc-cc.git
echo ""

echo Compilando...
cd $HOME/$WORKDIR/kafka-connector/kafka-connector
mvn install -DskipTests -T 1C

cd $HOME/$WORKDIR/poc-cc
mvn package -DskipTests -T 1C
echo ""

echo "(Re)Generando imágenes"
cd $HOME/$WORKDIR/poc-cc/poc-back/docker
./clean.sh
cd $HOME/$WORKDIR/poc-cc/poc-connectors/docker
./clean.sh
cd $HOME/$WORKDIR/poc-cc/poc-hub/docker
./clean.sh
cd $HOME/$WORKDIR/poc-cc/poc-web/docker
./clean.sh
cd $HOME/$WORKDIR/poc-cc/poc-kafka/docker
./clean.sh

cd $HOME/$WORKDIR/poc-cc/poc-back/docker
./build.sh
cd $HOME/$WORKDIR/poc-cc/poc-connectors/docker
./build.sh
cd $HOME/$WORKDIR/poc-cc/poc-hub/docker
./build.sh
cd $HOME/$WORKDIR/poc-cc/poc-web/docker
./build.sh
cd $HOME/$WORKDIR/poc-cc/poc-kafka/docker
./build.sh
echo ""

echo Limpiando
cd $HOME/$WORKDIR/
rm -rf $HOME/$WORKDIR/kafka-connector
cd $HOME/$WORKDIR/poc-cc
mvn clean -T 1C

cd ./compose
./clean.sh
echo ""
docker images
echo ""
pwd
echo ""
ls -l
echo ""

echo Listo!
echo ""