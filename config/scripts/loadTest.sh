#!/bin/bash

if [ "$#" -ne 2 ]; then
  LIMIT=300
  THREADS=2
else
  LIMIT=$1
  THREADS=$2
fi

echo "Iniciando prueba de carga ($LIMIT x $THREADS hilos)"
echo ""

cd $HOME/workspace/poc-cc/poc-hub
mvn com.smartbear.soapui:soapui-maven-plugin:5.2.1:loadtest -Dsoapui.loadtest="2 tps @ 5 min" -Dsoapui.limit=$LIMIT -Dsoapui.threadcount=$THREADS
#> mvn-output.log &

echo ""
