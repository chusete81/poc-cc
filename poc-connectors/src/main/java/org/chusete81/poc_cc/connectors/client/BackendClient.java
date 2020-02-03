package org.chusete81.poc_cc.connectors.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.common.Config;
import org.chusete81.poc_cc.common.model.BackendPayloadDTO;
import org.chusete81.poc_cc.common.model.PocDTO;
import org.chusete81.poc_cc.connectors.PocConnectorsApplication;
import org.chusete81.poc_cc.connectors.monitoring.ConnectorMonitoring;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class BackendClient {

    private static final Logger log = LogManager.getLogger(BackendClient.class);
    private static final Logger logMon = LogManager.getLogger(ConnectorMonitoring.class);

    private static final AtomicLong l = new AtomicLong();

    public PocDTO invoke (PocDTO pocDto) {
        long i = l.incrementAndGet();
        log.info(String.format("[%d] > Invocando a backend ...", i));

        int backendID = pocDto.getBackendId();
        String uuid = pocDto.getUuid();

        logMon.debug(String.format(ConnectorMonitoring.CONN_BACKEND_REQUEST, uuid, backendID));

        BackendPayloadDTO backendPayloadDTO = //BackendPayloadDTO.fromJson(pocDto.toJson());
                new BackendPayloadDTO(pocDto.getNif(), pocDto.getFcInicio(), pocDto.getFcFin(), "");

        HttpEntity<String> httpEntity = new HttpEntity<>(backendPayloadDTO.toJson());

        try {

            long startTime = new Date().getTime();

            // Establecer el valor de timeout definido para los backends
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Config.BACKEND_CONNECT_TIMEOUT_MS);
            factory.setReadTimeout(Config.getBackendTimeout());

            RestTemplate rt = new RestTemplate(factory);
            ResponseEntity<String> responseEntity = rt.postForEntity(
                    String.format("%s?backendId=%d", PocConnectorsApplication.getBackendUrl(), backendID),
                    httpEntity,
                    String.class);

            long elapsedTime = new Date().getTime() - startTime;

            log.info(String.format("[%d] < Recibida respuesta de backend en %d ms", i, elapsedTime));
            log.debug(responseEntity);
            logMon.debug(String.format(ConnectorMonitoring.CONN_BACKEND_RESPONSE, uuid, backendID, elapsedTime));

            String responseBody = responseEntity.getBody();
            backendPayloadDTO = BackendPayloadDTO.fromJson(responseBody);

        } catch (ResourceAccessException e) {

            log.error(e);
            logMon.debug(String.format(ConnectorMonitoring.CONN_BACKEND_TIMEOUT_ERROR, uuid, backendID));
            backendPayloadDTO.setResponse(e.getMessage());

        } catch (HttpServerErrorException e) {

            log.error(e);
            logMon.debug(String.format(ConnectorMonitoring.CONN_BACKEND_ERROR, uuid, backendID));
            backendPayloadDTO.setResponse(e.toString());

//        } catch (Exception e) {
//
//            log.error(e);
//            logMon.debug(String.format(ConnectorMonitoring.CONN_BACKEND_ERROR, uuid, backendID));
//            backendPayloadDTO.setResponse(e.toString());
//
        }

        pocDto.setResponse(backendPayloadDTO.getResponse());
        return pocDto;
    }
}
