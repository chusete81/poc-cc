package org.chusete81.poc_cc.connectors;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.common.Config;
import org.chusete81.poc_cc.common.Constants;
import org.chusete81.poc_cc.common.Utils;
import org.chusete81.poc_cc.connectors.daemon.RequestsProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocConnectorsApplication {

    // backendID(s) atendido(s) por este conector
    public static final Integer[] backendIDs = new Integer[] { 8, 13, 25, 32, 47, 54, 68, 72, 83, 95 };

    // configuracion de consumer threads
    private static final int MAX_NUM_CONSUMER_THREADS = 128;
    private static final int MIN_NUM_CONSUMER_THREADS = 1;
    private static final int DEFAULT_REQUESTS_NUM_CONSUMER_THREADS = 64; // hilos manejando peticiones de hub en capa de conectores
    private static final String NUM_CONSUMER_THREADS_ENV_VAR_NAME = "POC_CC_CONN_CONSUMER_THREADS";

    // consumers group id
    public static final String REQUESTS_GROUP_ID  = "poc-connectors-group-1";

    // url de backend por defecto
    private static final String DEFAULT_BACKEND_URL = "http://localhost:9000/invoke";

    private static final Logger log = LogManager.getLogger(PocConnectorsApplication.class);

    public static void main(String[] args) {
        log.info("POC_CC - Variables de entorno:");
        Utils.getPocCCEnvironment().forEach((k, v) -> log.info(String.format("%s: %s", k, v)));

        SpringApplication.run(PocConnectorsApplication.class, args);

        int numConsumerThreads = getConsumerThreads();
        log.info(String.format("Inicializando %d consumer threads (%s)", numConsumerThreads, Config.getKafkaServersURL()));
        initKafkaConsumerThreads(numConsumerThreads);
    }

    private static int getConsumerThreads() {
        int numConsumerThreads = DEFAULT_REQUESTS_NUM_CONSUMER_THREADS;

        String sAux = Utils.getEnvironmentVariable(NUM_CONSUMER_THREADS_ENV_VAR_NAME);
        try {
            int iAux = Integer.parseInt(sAux);

            if (iAux <= MAX_NUM_CONSUMER_THREADS && iAux >= MIN_NUM_CONSUMER_THREADS)
                numConsumerThreads = iAux;
            else
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            log.warn(String.format(
                    "Variable de entorno no establecida o valor no valido: %s=%s", NUM_CONSUMER_THREADS_ENV_VAR_NAME, sAux));
        }

        return numConsumerThreads;
    }

    private static void initKafkaConsumerThreads (int numKafkaConsumerThreads) {
        ExecutorService es = Executors.newFixedThreadPool(numKafkaConsumerThreads);

        for (int i=0; i < numKafkaConsumerThreads; i++) {
            log.debug("Inicializando kafkaConsumerThread");
            es.submit(new RequestsProcessor());
        }
    }

    public static String getBackendUrl() {
        String backendURL = DEFAULT_BACKEND_URL;

        backendURL = Optional.ofNullable(
                Utils.getEnvironmentVariable(Constants.BACKEND_URL_ENV_VAR_NAME))
                .orElse(backendURL);

        return backendURL;
    }

}
