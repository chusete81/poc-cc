package org.chusete81.poc_cc.hub;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.kafka_connector.subscriber.KafkaSubscriber;
import org.chusete81.poc_cc.common.Utils;
import org.chusete81.poc_cc.hub.daemon.ResponsesProcessorRunner;
import org.chusete81.poc_cc.hub.model.HubData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class PocHubApplication {

    private static HubData hubData = new HubData();
    private static KafkaSubscriber[] kafkaConsumerThreads;
    private static final Logger log = LogManager.getLogger(PocHubApplication.class);

    private static final int RESPONSES_NUM_CONSUMER_THREADS = 4;  // hilos manejando respuestas de backend en hub

    public static void main(String[] args) {
        log.info("POC_CC - Variables de entorno:");
        Utils.getPocCCEnvironment().forEach((k, v) -> log.info(String.format("%s: %s", k, v)));

        SpringApplication.run(PocHubApplication.class, args);
        initKafkaConsumerThreads(RESPONSES_NUM_CONSUMER_THREADS);
    }


    private static void initKafkaConsumerThreads (int numKafkaConsumerThreads) {
        log.debug("Inicializado consumer threads");

        ExecutorService es = Executors.newFixedThreadPool(numKafkaConsumerThreads);

        for (int i=0; i < numKafkaConsumerThreads; i++)
            es.submit(new ResponsesProcessorRunner());
    }


    protected static void stopKafkaConsumerThreads () {
        if (kafkaConsumerThreads == null)
            return;

        for (int i=0; i < kafkaConsumerThreads.length; i++)
            kafkaConsumerThreads[i].shutdown();
    }

    public static HubData getHubData() {
        return hubData;
    }
}

