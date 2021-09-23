package tr.org.liderahenk.liderconsole.core.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class RestClientTest {

	@Test
	public void authEntries() {
//		RestRequest request = new RestRequest();
//		request.setDnList(buildDnList());
//		request.setDnType(RestDNType.AHENK);
//		request.setPluginName("BROWSER");
//		request.setPluginVersion("1.0.0");
//		request.setCommandId("SAVE_PROFILE");
//		request.setParameterMap(buildParameterMap());
//		request.setPriority(Priority.HIGH);
//		RestResponse post = RestClient.getInstance().post(request);
//		Assert.assertNotNull(post);
//		System.out.println(post);
	}

	private List<String> buildDnList() {
		List<String> list = new ArrayList<String>();
		list.add("ou=Uncategorized,dc=mys,dc=pardus,dc=org");
		return list;
	}

	private Map<String, Object> buildParameterMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "test-value");
		return map;
	}

}
