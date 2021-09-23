package tr.org.liderahenk.service.model;

import tr.org.liderahenk.service.i18n.Messages;

public enum DesiredStatus {
	START, STOP, NA;

	/**
	 * Provide i18n message representation of the enum type.
	 * 
	 * @return
	 */
	public String getMessage() {
		return Messages.getString(this.toString());
	}

}