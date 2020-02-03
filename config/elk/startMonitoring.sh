#!/bin/bash

waitForURL() {
  if [ $URL ] ; then
    while [[ $(curl -v $URL 2> /dev/stdout > /dev/null | grep -c "HTTP/1.1 200") -lt 1 ]] ; do
      sleep 1
    done
	curl $URL
  fi
}


cd /home/poc/elk
./shutdownMonitoring.sh

echo ""
echo Levantando Elasticsearch
/home/poc/elk/elasticsearch/config/startElasticsearch.sh
URL=http://localhost:9200
waitForURL

echo ""
echo Levantando Logstash
/home/poc/elk/logstash/config/startLogstash.sh
URL=http://localhost:9600?pretty
waitForURL
echo ""

echo ""
echo Levantando Filebeat
/home/poc/elk/filebeat/startFilebeat.sh
sleep 1

echo ""
echo Levantando Kibana
/home/poc/elk/kibana/config/startKibana.sh
sleep 1

echo ""
ps -fu $(whoami) | grep elk | grep -v grep
ps -fu $(whoami) | grep filebeat | grep -v grep
ps -fu $(whoami) | grep node | grep cli

echo ""
