FROM ubuntu
ENV KAFKA_HOME=/opt/kafka
COPY ./files/* /tmp/
RUN apt-get update && \
	apt-get --yes --no-install-recommends install openjdk-8-jre-headless curl && \
	/tmp/installKafka.sh
ENTRYPOINT $KAFKA_HOME/bin/startKafka.sh