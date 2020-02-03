echo ""
echo Deteniendo Filebeat
ps -fu $(whoami) | grep filebeat | grep -v grep
ps -fu $(whoami) | grep filebeat | grep -v grep | awk '{ print "kill -SIGTERM " $2 }' | /bin/bash
sleep 1

echo ""
echo Deteniendo Logstash
ps -fu $(whoami) | grep logstash | grep -v grep
ps -fu $(whoami) | grep logstash | grep -v grep | awk '{ print "kill -SIGTERM " $2 }' | /bin/bash
sleep 3

echo ""
echo Deteniendo Kibana
ps -fu $(whoami) | grep node | grep cli
ps -fu $(whoami) | grep node | grep cli | awk '{ print "kill -SIGTERM " $2 }' | /bin/bash
sleep 1

echo ""
echo Deteniendo Elasticsearch
curl -X DELETE http://localhost:9200/poc_cc-* 2> /dev/null
echo ""
ps -fu $(whoami) | grep Elasticsearch | grep -v grep
ps -fu $(whoami) | grep Elasticsearch | grep -v grep | awk '{ print "kill -SIGTERM " $2 }' | /bin/bash
sleep 2

echo ""
echo Limpiando logs
rm -rf /home/poc/elk/filebeat/logs/*
rm -rf /home/poc/elk/logstash/logs/*
rm -rf /home/poc/elk/kibana/logs/*
rm -rf /home/poc/elk/elasticsearch/logs/*
rm -rf /tmp/elasticsearch.*

echo ""
