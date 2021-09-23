package tr.org.liderahenk.rsyslog.model;

import java.io.Serializable;

public class LogFileListItem implements Serializable {

	private static final long serialVersionUID = -4060913846099252209L;

	private String isLocal;
	private String recordDescription;
	private String logFilePath;

	public LogFileListItem() {
		super();
	}

	public LogFileListItem(String isLocal, String recordDescription, String logFilePath) {
		super();
		this.isLocal = isLocal;
		this.recordDescription = recordDescription;
		this.logFilePath = logFilePath;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String getRecordDescription() {
		return recordDescription;
	}

	public void setRecordDescription(String recordDescription) {
		this.recordDescription = recordDescription;
	}

	public String getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}

}
