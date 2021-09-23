package tr.org.liderahenk.backup.comparators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.liderconsole.core.model.CommandExecutionResult;
import tr.org.liderahenk.liderconsole.core.xmpp.enums.StatusCode;

public class ResultComparator implements Comparator<CommandExecutionResult> {

	private ObjectMapper objectMapper;

	public ResultComparator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public int compare(CommandExecutionResult o1, CommandExecutionResult o2) {
		if (o1.getResponseCode() == StatusCode.TASK_PROCESSED) {
			return -1;
		} else if (o2.getResponseCode() == StatusCode.TASK_PROCESSED) {
			return 1;
		}
		try {
			Map<String, Object> data1 = null;
			Map<String, Object> data2 = null;
			data1 = objectMapper.readValue(o1.getResponseData(), 0, o1.getResponseData().length,
					new TypeReference<HashMap<String, Object>>() {
					});
			data2 = objectMapper.readValue(o2.getResponseData(), 0, o2.getResponseData().length,
					new TypeReference<HashMap<String, Object>>() {
					});
			return new Integer(data2.get("percentage").toString())
					.compareTo(new Integer(data1.get("percentage").toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o2.getCreateDate().compareTo(o1.getCreateDate());
	}

}
