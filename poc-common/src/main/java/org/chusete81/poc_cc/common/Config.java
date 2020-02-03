package org.chusete81.poc_cc.common;

public class Config {
    // Kafka server
    private static final String DEFAULT_KAFKA_SERVERS_URL = "localhost:29000";

    // Timeouts
    private static final int DEFAULT_GLOBAL_USER_TIMEOUT_MS = 20000;

    // Backend timeout
    public static final int BACKEND_CONNECT_TIMEOUT_MS = 5000;
    private static final int BACKEND_READ_TIMEOUT_MS = 10000;

    public static int getBackendTimeout () {
        int backendTimeout = BACKEND_READ_TIMEOUT_MS;

        String sAux = Utils.getEnvironmentVariable(Constants.BACKEND_READ_TIMEOUT_ENV_VAR_NAME);

        if (sAux != null) {
            try {
                backendTimeout = Integer.parseInt(sAux);
            } catch (NumberFormatException e) {
                throw new ConfigException(String.format(
                        "Error: valor no valido para variable de entorno %s: %s", Constants.BACKEND_READ_TIMEOUT_ENV_VAR_NAME, sAux));
            }
        }

        return backendTimeout;
    }

    public static int getGlobalUserTimeoutMS () {
        int globalUserTimeout = DEFAULT_GLOBAL_USER_TIMEOUT_MS;

        String sAux = Utils.getEnvironmentVariable(Constants.GLOBAL_TIMEOUT_ENV_VAR_NAME);

        if (sAux != null) {
            try {
                globalUserTimeout = Integer.parseInt(sAux);
            } catch (NumberFormatException e) {
                throw new ConfigException(String.format(
                        "Error: valor no valido para variable de entorno %s: %s", Constants.GLOBAL_TIMEOUT_ENV_VAR_NAME, sAux));
            }
        }

        return globalUserTimeout;
    }

    public static String getKafkaServersURL () throws RuntimeException {
        String kafkaURL = DEFAULT_KAFKA_SERVERS_URL;

        try {
            String sAux = Utils.getEnvironmentVariable(Constants.KAFKA_SERVERS_ENV_VAR_NAME);
            if (sAux != null)
                kafkaURL = sAux;
        } catch (Exception e) {
            // TODO hacer obligatoria la variable?
            //throw new ConfigException(String.format("Error: variable de entorno requerida: %s", KAFKA_SERVERS_ENV_VAR_NAME));
        }
        return kafkaURL;
    }
}
