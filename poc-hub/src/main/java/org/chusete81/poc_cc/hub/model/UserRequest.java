package org.chusete81.poc_cc.hub.model;

import org.chusete81.poc_cc.common.model.PocDTO;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class UserRequest {

    private String uuid;
    private long createTimestamp;
    private long updateTimestamp;
    private Map<Integer, PocDTO> partialResponsesMap = null;

    public UserRequest (String uuid) {
        if (uuid == null)
            throw new RuntimeException("UUID no puede ser nulo");

        this.uuid = uuid;
        this.createTimestamp = new Date().getTime();
        this.updateTimestamp = createTimestamp;

        partialResponsesMap = new HashMap<Integer, PocDTO>();
    }

    public String getUuid() {
        return uuid;
    }

    public PocDTO getBackendResponse (int backendId) {
        PocDTO dto = partialResponsesMap.get(backendId);
        return dto;
    }

    public void putBackendResponse (PocDTO pocDTO) {
        partialResponsesMap.put(pocDTO.getBackendId(), pocDTO);
        this.updateTimestamp = new Date().getTime();
    }

    public Map<Integer, PocDTO> getPartialResponses() {
        return partialResponsesMap;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public long lastResponseTime () {
        return this.updateTimestamp - this.createTimestamp;
    }

}
