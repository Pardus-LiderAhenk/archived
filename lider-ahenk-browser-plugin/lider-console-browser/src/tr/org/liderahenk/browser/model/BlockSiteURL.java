package tr.org.liderahenk.browser.model;

import java.io.Serializable;

public class BlockSiteURL implements Serializable {

	private static final long serialVersionUID = 16380325829584768L;
	
	private String URL;
	
	private String description;
	
	public BlockSiteURL() {
		super();
	}

	public BlockSiteURL(String URL, String description) {
		super();
		this.URL = URL;
		this.description = description;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
