package tr.org.liderahenk.packagemanager.model;

import tr.org.liderahenk.packagemanager.i18n.Messages;

public enum DesiredPackageStatus {
	UNINSTALL, NA;

	/**
	 * Provide i18n message representation of the enum type.
	 * 
	 * @return
	 */
	public String getMessage() {
		return Messages.getString(this.toString());
	}

}
