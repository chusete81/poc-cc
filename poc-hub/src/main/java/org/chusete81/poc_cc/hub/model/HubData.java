package org.chusete81.poc_cc.hub.model;

import org.chusete81.poc_cc.common.model.PocDTO;

import java.util.HashMap;
import java.util.Map;

public class HubData {

	private Map<String, UserRequest> userRequestsMap = new HashMap<String, UserRequest>();
	
	public void newUserRequest (UserRequest userRequest) {
		String uuid = userRequest.getUuid();
		
		if (uuid == null)
			throw new RuntimeException("UUID no puede ser nulo");
		
		if (userRequestsMap.containsKey(uuid))
			throw new RuntimeException ("Generado un UUID repetido");
		
		userRequestsMap.put(uuid, userRequest);
	}
	
	public UserRequest getUserRequest (String uuid) {
		if (userRequestsMap.containsKey(uuid))
			return userRequestsMap.get(uuid);
		else
			throw new RuntimeException ("UUID solicitado no encontrado");
	}	
	
	public void putUserRequest (PocDTO pocDTO) {
		if (userRequestsMap.containsKey(pocDTO.getUuid()))
			userRequestsMap.get(pocDTO.getUuid()).putBackendResponse(pocDTO);
	}

	public boolean isUserRequestProcessed (String uuid) {
		UserRequest userRequest = userRequestsMap.get(uuid);

		Map<Integer, PocDTO> partialResponsesMap = userRequest.getPartialResponses();
		for (Integer i : partialResponsesMap.keySet()) {
			PocDTO dto = partialResponsesMap.get(i);
			if (dto.getResponse() == null)
				return false;
		}

		return true;
	}

	public Map<String, UserRequest> getUserRequestsMap() {
		return userRequestsMap;
	}
}
