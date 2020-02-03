package org.chusete81.poc_cc.web;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class PocWebApplication {

    private static final String DEFAULT_HUB_ENDPOINT = "http://localhost:8888";
    private static final String HUB_ENDPOINT_ENV_VAR_NAME = "POC_CC_HUB_ENDPOINT";

    private static final Logger log = LogManager.getLogger(PocWebApplication.class);

    public static void main(String[] args) {
        log.info("POC_CC - Variables de entorno:");
        Utils.getPocCCEnvironment().forEach((k, v) -> log.info(String.format("%s: %s", k, v)));

        SpringApplication.run(PocWebApplication.class, args);
    }

    public static String getHubEndpoint() {
        String hubEndpoint = DEFAULT_HUB_ENDPOINT;

        hubEndpoint = Optional.ofNullable(
                Utils.getEnvironmentVariable(HUB_ENDPOINT_ENV_VAR_NAME))
                .orElse(hubEndpoint);

        return hubEndpoint;
    }
}

