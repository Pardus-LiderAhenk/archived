package tr.org.liderahenk.resourceusage.model;

import java.io.Serializable;

public class ResourceUsageAlertItem implements Serializable {

	private static final long serialVersionUID = -4885338502414393L;

	private String type;
	private String limit;
	private String email;

	public ResourceUsageAlertItem() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public ResourceUsageAlertItem(String type, String limit, String email) {
		super();
		this.type = type;
		this.limit = limit;
		this.email = email;
	}
}
