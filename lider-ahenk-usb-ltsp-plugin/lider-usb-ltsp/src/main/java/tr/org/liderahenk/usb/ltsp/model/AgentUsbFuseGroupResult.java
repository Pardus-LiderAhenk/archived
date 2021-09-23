package tr.org.liderahenk.usb.ltsp.model;

import java.io.Serializable;

public class AgentUsbFuseGroupResult implements Serializable {

	private static final long serialVersionUID = 6884166036936677901L;

	private String username;

	private String statusCode;

	public AgentUsbFuseGroupResult() {
	}

	public AgentUsbFuseGroupResult(String username, String statusCode) {
		this.username = username;
		this.statusCode = statusCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
