export JAVA_HOME=/home/poc/jdk1.8.0_161
cd /home/poc/elk/logstash/bin
nohup ./logstash > ../logs/logstash.log &
#sleep 2
#tail -10f ../logs/logstash.log
