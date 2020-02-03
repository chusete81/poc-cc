package org.chusete81.poc_cc.common;

public class Constants {
    public static final long MS_PER_YEAR = 365 * 24 * 3600 * 1000;

    // Kafka topics
    public static final String REQUESTS_QUEUE_KAFKA_TOPIC  = "pocRequestsQueue";
    public static final String RESPONSES_QUEUE_KAFKA_TOPIC = "pocResponsesQueue";

    // Environment variables names
    public static final String KAFKA_SERVERS_ENV_VAR_NAME = "POC_CC_KAFKA_URL";
    public static final String GLOBAL_TIMEOUT_ENV_VAR_NAME = "POC_CC_GLOBAL_USER_TIMEOUT";
    public static final String BACKEND_READ_TIMEOUT_ENV_VAR_NAME = "POC_CC_BACKEND_TIMEOUT";
    public static final String ERROR_PERC_ENV_VAR_NAME = "POC_CC_BACK_ERROR_PERC";
    public static final String BACKEND_URL_ENV_VAR_NAME = "POC_CC_CONN_BACKEND_URL";

}
