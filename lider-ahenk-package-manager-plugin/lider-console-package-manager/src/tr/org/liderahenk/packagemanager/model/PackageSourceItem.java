package tr.org.liderahenk.packagemanager.model;

import java.io.Serializable;

public class PackageSourceItem implements Serializable {

	private static final long serialVersionUID = -3772888267820540440L;

	private String url;

	public PackageSourceItem() {
		super();
	}

	public PackageSourceItem(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
