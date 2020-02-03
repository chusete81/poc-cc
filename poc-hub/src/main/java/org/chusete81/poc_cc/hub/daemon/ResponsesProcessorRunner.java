package org.chusete81.poc_cc.hub.daemon;

import org.chusete81.poc_cc.hub.PocHubApplication;
import org.chusete81.poc_cc.hub.monitoring.HubMonitoring;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.kafka_connector.subscriber.KafkaSubscriber;
import org.chusete81.poc_cc.common.Config;
import org.chusete81.poc_cc.common.Constants;
import org.chusete81.poc_cc.common.model.PocDTO;

import java.util.Date;

public class ResponsesProcessorRunner extends KafkaSubscriber {

    private static final String KAFKA_SERVERS_URL = Config.getKafkaServersURL();
    private static final String RESPONSES_GROUP_ID = "poc-hub-group";

    private static final Logger log = LogManager.getLogger(ResponsesProcessorRunner.class);
    private static final Logger logMon = LogManager.getLogger(HubMonitoring.class);

    public void run() {
        log.debug(String.format("[ResponsesProcessorRunner.run()]: %s@%s",
                Constants.RESPONSES_QUEUE_KAFKA_TOPIC, KAFKA_SERVERS_URL));

        super.run(KAFKA_SERVERS_URL, Constants.RESPONSES_QUEUE_KAFKA_TOPIC, RESPONSES_GROUP_ID);
    }

    protected void procesarMensaje (String msg) {
        log.debug("Respuesta recibida de conector");
        log.debug(String.format("  Procesando respuesta de backend: %s", msg.replace('\r', ' ').replace('\n', ' ')));

        PocDTO dto;
        try {
            dto = PocDTO.fromJson(msg);
            dto.setHubResponseTimestamp(new Date().getTime());
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("Respuesta de conector incorrecta");
        }

        PocHubApplication.getHubData().putUserRequest(dto);

        int backendID = dto.getBackendId();
        String uuid = dto.getUuid();

        log.debug(String.format(" < Actualizando respuesta - UUID: %s, backendId: %d", uuid, backendID));
        logMon.debug(String.format(HubMonitoring.HUB_CONN_RESPONSE, uuid, backendID));

        if (PocHubApplication.getHubData().isUserRequestProcessed(uuid))
            logMon.debug(String.format(
                    HubMonitoring.HUB_PROCESSED,
                    uuid,
                    PocHubApplication.getHubData().getUserRequest(uuid).lastResponseTime()));
    }
}
