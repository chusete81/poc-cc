package org.chusete81.poc_cc.hub.listeners;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.hub.services.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HubListener {

	private static final Logger log = LogManager.getLogger(HubListener.class);
	private static AtomicLong seq = new AtomicLong();

	@Autowired
	private HubService service;
	
	@GetMapping(value="/status", produces="text/plain")
	public String getStatus () {
		return service.getGlobalStatus();
	}
	
	@GetMapping(value="/getStatus", produces="text/plain")
	public String getStatus (@RequestParam(value="uuid") String uuid) {
		return service.getStatus(uuid);
	}
	
	@GetMapping(value="/newRequest", produces="text/plain")
	public String newUserRequest (@RequestParam(value="nif", defaultValue="") String nif) {
		
		long reqId = seq.incrementAndGet();
		log.info(String.format("[%d] Peticion de usuario entrante...", reqId));
		
		if (nif == null || nif.isEmpty()) {
			log.warn(String.format("[%d] Falta parametro requerido", reqId));
			return null;
		} else {
			nif = nif.toUpperCase();
		}
		
		log.debug(String.format("[%d]   Procesando NIF %s", reqId, nif));

		String uuid = service.newUserRequest(nif);
		
		log.info(String.format("[%d] Peticion de usuario registrada  UUID [%s], timestamp [%d]", reqId, uuid, new Date().getTime()));
		
		return uuid;
	}

}
