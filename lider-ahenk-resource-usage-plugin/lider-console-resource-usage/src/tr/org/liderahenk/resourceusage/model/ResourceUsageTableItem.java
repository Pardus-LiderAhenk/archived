package tr.org.liderahenk.resourceusage.model;

import java.io.Serializable;

public class ResourceUsageTableItem implements Serializable{

	private static final long serialVersionUID = -8577285325029551133L;
	private String recordDate;
	private String memUsed;
	private String cpuUsed;
	public ResourceUsageTableItem(String recordDate, String memUsed, String cpuUsed) {
		super();
		this.recordDate = recordDate;
		this.memUsed = memUsed;
		this.cpuUsed = cpuUsed;
	}
	public ResourceUsageTableItem() {
		super();
	}
	public String getRecordDate() {
		return recordDate;
	}
	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}
	public String getMemUsed() {
		return memUsed;
	}
	public void setMemUsed(String memUsed) {
		this.memUsed = memUsed;
	}
	public String getCpuUsed() {
		return cpuUsed;
	}
	public void setCpuUsed(String cpuUsed) {
		this.cpuUsed = cpuUsed;
	}
}
