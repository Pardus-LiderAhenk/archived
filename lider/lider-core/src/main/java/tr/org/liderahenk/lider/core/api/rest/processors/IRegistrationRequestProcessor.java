package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

public interface IRegistrationRequestProcessor {
	
	IRestResponse add(String requestBodyDecoded);

	IRestResponse list();

	IRestResponse get(Long id);
	
	IRestResponse delete(Long id);


}
