package org.chusete81.poc_cc.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackendPayloadDTO {

    private String nif;
    private long fcInicio;
    private long fcFin;
    private String response;

    public static BackendPayloadDTO fromJson (String json) {
        try {
            BackendPayloadDTO backendPayloadDTO = new ObjectMapper().readValue(json, BackendPayloadDTO.class);
            return backendPayloadDTO;
        } catch (IOException e) {
            throw new RuntimeException("Invalid payload for BackendPayloadDTO", e);
        }
    }

    public String toJson () {
        try {
            String jsonString = new ObjectMapper().writeValueAsString(this);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
