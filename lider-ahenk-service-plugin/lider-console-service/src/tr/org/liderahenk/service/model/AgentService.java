package tr.org.liderahenk.service.model;

import java.util.List;

public class AgentService {
	
	private String agent;
	private List<ServiceListItem> serviceList;
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public List<ServiceListItem> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<ServiceListItem> serviceList) {
		this.serviceList = serviceList;
	}

}
