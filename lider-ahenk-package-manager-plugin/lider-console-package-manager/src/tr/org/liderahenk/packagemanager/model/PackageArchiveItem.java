package tr.org.liderahenk.packagemanager.model;

import java.io.Serializable;

public class PackageArchiveItem implements Serializable {

	private static final long serialVersionUID = 8410790905721952374L;
	private String packageName;
	private String version;
	private String installationDate;
	private String operation;

	public PackageArchiveItem(String version, String installationDate, String packageName, String operation) {
		super();
		this.version = version;
		this.installationDate = installationDate;
		this.packageName = packageName;
		this.operation = operation;
	}

	public PackageArchiveItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
