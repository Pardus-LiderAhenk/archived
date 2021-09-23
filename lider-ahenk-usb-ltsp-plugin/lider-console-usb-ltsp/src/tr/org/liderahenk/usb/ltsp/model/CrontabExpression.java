package tr.org.liderahenk.usb.ltsp.model;

import tr.org.liderahenk.usb.ltsp.enums.ScheduleOperation;

public class CrontabExpression {
	private ScheduleOperation operation;
	private String crontabStr;
	private boolean active;

	public ScheduleOperation getOperation() {
		return operation;
	}

	public void setOperation(final ScheduleOperation operation) {
		this.operation = operation;
	}

	/**
	 * 
	 * @return cron expression. Example: 0 0 12 1/1 * ? *
	 */
	public String getCrontabStr() {
		return crontabStr;
	}

	public void setCrontabStr(final String crontabStr) {
		this.crontabStr = crontabStr;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}
}
