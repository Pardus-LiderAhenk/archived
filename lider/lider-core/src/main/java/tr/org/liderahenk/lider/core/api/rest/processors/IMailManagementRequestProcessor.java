package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

public interface IMailManagementRequestProcessor {
	
	IRestResponse add(String requestBodyDecoded);

	IRestResponse update(String requestBodyDecoded);

	IRestResponse list(String pluginName, String pluginVersion);

	IRestResponse get(Long id);

	IRestResponse delete(Long id);

}
