package org.chusete81.poc_cc.back;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocBackApplication {

    private static final Logger log = LogManager.getLogger(PocBackApplication.class);

    public static void main(String[] args) {
        log.info("POC_CC - Variables de entorno:");
        Utils.getPocCCEnvironment().forEach((k, v) -> log.info(String.format("%s: %s", k, v)));

        SpringApplication.run(PocBackApplication.class, args);
    }
}

