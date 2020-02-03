package org.chusete81.poc_cc.web.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.web.PocWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HubClient {

    private static final String HUB_ENDPOINT = PocWebApplication.getHubEndpoint();

    private static final Logger log = LogManager.getLogger(HubClient.class);

    public String newRequest (String nif) {
        log.info(String.format("[HubClient.newRequest(%s)]", nif));
        log.debug(String.format("Invocando a Hub  NIF: %s", nif));

        ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(
                String.format("%s/newRequest?nif=%s", HUB_ENDPOINT, nif), String.class);

        String uuid = responseEntity.getBody();

        log.debug(String.format("Respuesta de Hub  UUID: %s", uuid));

        return uuid;
    }

    public static String getStatus (String uuid) {
        log.info(String.format("[HubClient.getStatus(%s)]", uuid));

        ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(
                String.format("%s/getStatus?uuid=%s", HUB_ENDPOINT, uuid), String.class);

        String status = responseEntity.getBody();
        return status;
    }
}
