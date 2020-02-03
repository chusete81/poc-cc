package org.chusete81.poc_cc.hub.services;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.kafka_connector.publisher.KafkaPublisher;
import org.chusete81.poc_cc.common.Config;
import org.chusete81.poc_cc.common.Constants;
import org.chusete81.poc_cc.common.model.PocDTO;
import org.springframework.stereotype.Service;

import org.chusete81.poc_cc.hub.PocHubApplication;
import org.chusete81.poc_cc.hub.model.UserRequest;
import org.chusete81.poc_cc.hub.monitoring.HubMonitoring;

@Service
public class HubService {

    private static final Logger log = LogManager.getLogger(HubService.class);
    private static final Logger logMon = LogManager.getLogger(HubMonitoring.class);

    private static final String topic = Constants.REQUESTS_QUEUE_KAFKA_TOPIC;


    public String newUserRequest (String nif) {
        Date now = new Date();
        return newUserRequest (
                nif,
                now.getTime() - Constants.MS_PER_YEAR,
                now.getTime());
    }

    public String newUserRequest (String nif, long fcInicio, long fcFin) {
        String uuid = generateUUID();

        log.info(String.format("Nueva peticion UUID [%s], NIF [%s]", uuid, nif));

        PocHubApplication.getHubData().newUserRequest(new UserRequest(uuid));
        logMon.debug(String.format(HubMonitoring.HUB_NEW_REQUEST, uuid));

        connectorCalls(nif, fcInicio, fcFin, uuid);

        return uuid;
    }

    private void connectorCalls(String nif, long fcInicio, long fcFin, String uuid) {

        // IDs de backends
        int[] backendIDs = new int[] { 8, 13, 25, 32, 47, 54, 68, 72, 83, 95 };

        for (int i=0; i < backendIDs.length; i++) {

            int backendId = backendIDs[i];
            log.debug(String.format(" > Mensaje a conector - UUID: %s, backendId: %d", uuid, backendId));

            PocDTO pocDTO = new PocDTO(uuid, backendId, nif, fcInicio, fcFin, null, new Date().getTime(), 0l);
            String msg = pocDTO.toJson();

            log.debug(" - Encolando peticion a backend");

            // enviar peticion unitaria a backend
            KafkaPublisher.publish(Config.getKafkaServersURL(), topic, msg);

            log.debug(" - Peticion a backend encolada");
            logMon.debug(String.format(HubMonitoring.HUB_MSG_TO_CONN, uuid, backendId));

            // guardar nueva peticion a backend a la espera de respuesta
            PocHubApplication.getHubData().getUserRequest(uuid).putBackendResponse(pocDTO);
        }
    }


    public String getGlobalStatus () {
        StringBuilder sb = new StringBuilder("GLOBAL STATUS\n-------------\n\n");

        Map<String, UserRequest> userRequests = PocHubApplication.getHubData().getUserRequestsMap();

        Map<String, String> sortedKeyMap = new TreeMap<String, String>(Collections.reverseOrder());
        for (String uuid : userRequests.keySet()) {
            UserRequest userRequest = userRequests.get(uuid);
            String sortedKey = String.format("%d-%s", userRequest.getCreateTimestamp(), userRequest.getUuid());
            sortedKeyMap.put(sortedKey, uuid);
        }

        for (String sortedKey : sortedKeyMap.keySet()) {
            String uuid = sortedKeyMap.get(sortedKey);
            UserRequest userRequest = userRequests.get(uuid);

            if (new Date().getTime() - userRequest.getCreateTimestamp() < 2 * Config.getGlobalUserTimeoutMS())
                sb.append(getStatus(userRequest));
        }

        return sb.toString();
    }

    public String getStatus (String uuid) {
        return getStatus (PocHubApplication.getHubData().getUserRequest(uuid));
    }

    private String getStatus (UserRequest userRequest) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(
                "UserRequestUUID: %s  -  UserRequestTimestamp: %s  -  ElapsedTime: %d ms  -  LastResponseTime: %d \n",
                userRequest.getUuid(),
                new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(userRequest.getCreateTimestamp())),
                new Date().getTime() - userRequest.getCreateTimestamp(),
                userRequest.lastResponseTime()));

        Map<Integer, PocDTO> partialResponses = userRequest.getPartialResponses();

        for (Integer backendId : partialResponses.keySet()) {
            PocDTO dto = partialResponses.get(backendId);

            String backendResponseTime = (dto.getResponse() == null) ? "n/d"  : String.valueOf(dto.getHubResponseTimestamp() - userRequest.getCreateTimestamp());
            String backendResponse     = (dto.getResponse() == null) ? "null" : "\"" + dto.getResponse() + "\"";

            sb.append(String.format(
                    " - backendID: %d, responseTime: %s, response: %s\n",
                    dto.getBackendId(),
                    backendResponseTime,
                    backendResponse));
        }

        sb.append("\n");
        return sb.toString();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
        //return String.valueOf((long) (Long.MAX_VALUE * Math.random()));
    }
}
