package org.chusete81.poc_cc.connectors.daemon;

import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.chusete81.kafka_connector.publisher.KafkaPublisher;
import org.chusete81.kafka_connector.subscriber.KafkaSubscriber;
import org.chusete81.poc_cc.connectors.PocConnectorsApplication;
import org.chusete81.poc_cc.connectors.client.BackendClient;
import org.chusete81.poc_cc.connectors.monitoring.ConnectorMonitoring;
import org.chusete81.poc_cc.common.Config;
import org.chusete81.poc_cc.common.Constants;
import org.chusete81.poc_cc.common.model.PocDTO;

public class RequestsProcessor extends KafkaSubscriber {

    private static final Logger log = LogManager.getLogger(RequestsProcessor.class);
    private static final Logger logMon = LogManager.getLogger(ConnectorMonitoring.class);

    public void run() {
        super.run(
                Config.getKafkaServersURL(),
                Constants.REQUESTS_QUEUE_KAFKA_TOPIC,
                PocConnectorsApplication.REQUESTS_GROUP_ID);
    }

    @Override
    protected void procesarMensaje (String msg) {
        log.info(">>> Peticion entrante de hub");
        log.debug(String.format("Procesando mensaje: worker %s", this.toString()));
        log.debug(String.format(" - msg: %s", msg.replace('\r', ' ').replace('\n', ' ')));

        PocDTO dto = null;

        try {
            dto = PocDTO.fromJson(msg);
        } catch (Exception e) {
            log.error(String.format("Peticion de hub incorrecta: %s", msg));
            log.error(e);
            return;
        }

        int backendID = dto.getBackendId();
        String uuid = dto.getUuid();

        // Comprobar si la peticion va dirigida a este conector (backend), en caso contrario se ignora
        if (!Arrays.asList(PocConnectorsApplication.backendIDs).contains(backendID)) {
            log.debug(String.format("<<< Ignorando peticion destinada al backendID %d", backendID));
            return;
        }

        // Controlar timeout de plataforma
        long elapsedTime = new Date().getTime() - dto.getHubRequestTimestamp();
        log.debug(String.format(" - uuid: %s, queuedTime: %d", uuid, elapsedTime));
        logMon.debug(String.format(ConnectorMonitoring.CONN_INCOMING_MSG, uuid, backendID, elapsedTime));

        if (elapsedTime < Config.getGlobalUserTimeoutMS()) {

            // Invocacion a backend
            dto = new BackendClient().invoke(dto);

        } else {

            // Si el timestamp de la peticion establecido por el Hub es demasiado antiguo,
            // no llamar al backend y devolver un mensaje de 'error por timeout de la plataforma'
            // (el solicitante ya no esta esperando por una respuesta, evitamos aumentar congestion)
            log.error(
                    String.format("Timeout de plataforma: se ignora peticion para backend caducada - " +
                                    "uuid: %s, backendId: %d, queuedTime: %d", uuid, backendID, elapsedTime));

            logMon.debug(String.format(ConnectorMonitoring.CONN_PLATFORM_TIMEOUT_ERROR, uuid, backendID));

            dto.setResponse("Timeout de plataforma: no se invoca a backend");
        }

        log.debug("Encolando respuesta de backend");

        KafkaPublisher.publish(
                Config.getKafkaServersURL(),
                Constants.RESPONSES_QUEUE_KAFKA_TOPIC,
                dto.toJson());

        log.debug("Respuesta de backend encolada");

        log.info("<<< Enviada respuesta a hub");
        logMon.debug(String.format(ConnectorMonitoring.CONN_CONNECTOR_RESPONSE_MSG, uuid, backendID));
    }
}
