ps -Fu poc | grep logstash | grep -v grep | awk '{print "kill -SIGTERM " $2}' | /bin/bash
