package tr.org.liderahenk.resourceusage.model;

import java.io.Serializable;

public class ResourceUsageAlertTableItem implements Serializable {

	private static final long serialVersionUID = 889567668959736922L;
	private String date;
	private String usage;
	private String pattern;
	private String action;
	private String message;
	public ResourceUsageAlertTableItem(String date, String usage, String pattern, String action, String message) {
		super();
		this.date = date;
		this.usage = usage;
		this.pattern = pattern;
		this.action = action;
		this.message = message;
	}
	public ResourceUsageAlertTableItem() {
		super();
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
