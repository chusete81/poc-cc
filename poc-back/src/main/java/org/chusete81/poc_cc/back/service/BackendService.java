package org.chusete81.poc_cc.back.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.back.exception.BackendException;
import org.chusete81.poc_cc.back.monitoring.BackendMonitoring;
import org.chusete81.poc_cc.common.Config;
import org.chusete81.poc_cc.common.Constants;
import org.chusete81.poc_cc.common.Utils;
import org.chusete81.poc_cc.common.model.BackendPayloadDTO;
import org.springframework.stereotype.Service;

@Service
public class BackendService {
    private static final int DEFAULT_ERROR_PERC = 1;

    private static final Logger log = LogManager.getLogger(BackendService.class);
    private static final Logger logMon = LogManager.getLogger(BackendMonitoring.class);

    public BackendPayloadDTO procesar (BackendPayloadDTO backendPayloadDTO) throws BackendException {
        BackendPayloadDTO dto = simularProcesamiento (backendPayloadDTO);
        return dto;
    }

    private BackendPayloadDTO simularProcesamiento (BackendPayloadDTO backendPayloadDTO) throws BackendException {

        esperar();

        generarPosibleError();

        backendPayloadDTO.setResponse(String.format("Respuesta a peticion"));
        return backendPayloadDTO;
    }

    private void generarPosibleError() throws BackendException {
        // Probabilidad de error
        int errPerc = DEFAULT_ERROR_PERC;

        String sAux = Utils.getEnvironmentVariable(Constants.ERROR_PERC_ENV_VAR_NAME);
        if (sAux != null) {
            try {
                errPerc = Integer.parseInt(sAux);
            } catch (NumberFormatException e) {
                log.warn(String.format(
                        "Variable de entorno no establecida o valor no valido: %s=%s", Constants.ERROR_PERC_ENV_VAR_NAME, sAux));
            }
        }

        if (Math.random() * 100 > 100 - errPerc) {
            logMon.debug(BackendMonitoring.BACK_ERROR);
            throw new BackendException(String.format("Este backend devuelve un %d%% de errores", errPerc));
        }
    }

    private void esperar() {
        final long MAX_WAIT = Config.getBackendTimeout() * 105 / 100; // +5%

        try {
            long timeout = (long) (Math.random() * MAX_WAIT);

            timeout = timeout * timeout / MAX_WAIT; // mejora sencilla de los tiempos medios

            log.debug(String.format("       [...] Esperando durante %d ms...", timeout));
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            log.error(e);
        }
    }
}
