#!/bin/bash

VER_BACK=2.0.0
VER_CONN=2.0.0
VER_HUB=2.0.0
VER_WEB=2.0.0

cd $HOME
WORKDIR=workspace-$(date +%y%m%d%H%M%S%N)
mkdir $WORKDIR
cd $HOME/$WORKDIR
mkdir target
echo ""

echo Descargando codigo fuente
git clone -v https://chusete81@github.com/chusete81/kafka-connector.git
echo ""
git clone -v https://chusete81@github.com/chusete81/poc-cc.git
echo ""

echo Compilando...
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
