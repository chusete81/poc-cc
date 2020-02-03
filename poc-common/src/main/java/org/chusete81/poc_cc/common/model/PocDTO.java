package org.chusete81.poc_cc.common.model;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PocDTO {

    private String uuid;
    private int backendId;
    private String nif;
    private long fcInicio;
    private long fcFin;
    private String response;
    private long hubRequestTimestamp;
    private long hubResponseTimestamp;

    public static PocDTO fromJson (String json) {
        try {
            PocDTO pocDTO = new ObjectMapper().readValue(json, PocDTO.class);
            return pocDTO;
        } catch (IOException e) {
            throw new RuntimeException("Invalid payload for PocDTO", e);
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
