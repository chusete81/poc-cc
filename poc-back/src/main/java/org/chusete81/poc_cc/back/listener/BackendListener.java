package org.chusete81.poc_cc.back.listener;

import org.chusete81.poc_cc.back.exception.BackendException;
import org.chusete81.poc_cc.back.monitoring.BackendMonitoring;
import org.chusete81.poc_cc.back.service.BackendService;
import org.chusete81.poc_cc.common.model.BackendPayloadDTO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class BackendListener {

    private static final Logger log = LogManager.getLogger(BackendListener.class);
    private static final Logger logMon = LogManager.getLogger(BackendMonitoring.class);

    @Autowired
    private BackendService backendService;

    private static AtomicLong atomicLong = new AtomicLong();

    @RequestMapping(path="/invoke", method=RequestMethod.POST, produces="application/json")
    public ResponseEntity<String> invoke (@RequestParam(value="backendId", defaultValue="1") String backendId, @RequestBody() String payload) {
        long startTime = new Date().getTime();
        long i = atomicLong.incrementAndGet();
        log.info(String.format("[%d] Peticion entrante a backend %s", i, backendId));
        logMon.debug(BackendMonitoring.BACK_REQUEST);

        BackendPayloadDTO backendPayloadDTO;

        try {
            log.debug(String.format(" > payload: [%s]", payload.replace('\r', ' ').replace('\n', ' ')));
            backendPayloadDTO = BackendPayloadDTO.fromJson(payload);
            log.debug(backendPayloadDTO);
        } catch (RuntimeException e) {
            log.error(String.format("[%d] Error: %s", i, e.getMessage()));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            backendPayloadDTO = backendService.procesar(backendPayloadDTO);
        } catch (BackendException e) {
            log.error(String.format("[%d] Error: %s", i, e.getMessage()));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.debug(String.format(" < res: %s", backendPayloadDTO.toString()));

        long elapsedTime = new Date().getTime() - startTime;
        log.info(String.format("[%d] Respuesta en %d ms", i, elapsedTime));
        logMon.debug(String.format(BackendMonitoring.BACK_RESPONSE, elapsedTime));

        return new ResponseEntity<>(backendPayloadDTO.toJson(), HttpStatus.OK);
    }
}
