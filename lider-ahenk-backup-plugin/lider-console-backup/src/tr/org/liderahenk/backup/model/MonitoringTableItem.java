package tr.org.liderahenk.backup.model;

import java.io.Serializable;

public class MonitoringTableItem implements Serializable {

	private static final long serialVersionUID = 4882144394899651394L;

	private String dn;
	private String percentage;
	private String estimation;
	private String numberOfFiles;
	private String totalFileSize;
	private String estimatedTransferSize;
	private String numberOfTransferredFiles;
	private String numberOfCreatedFiles;
	private String transferredFileSize;

	private boolean ongoing;
	private boolean successful;

	public MonitoringTableItem(String dn, String percentage, String estimation, String numberOfFiles,
			String totalFileSize, String estimatedTransferSize, String numberOfTransferredFiles,
			String numberOfCreatedFiles, String transferredFileSize) {
		this.dn = dn;
		this.percentage = percentage;
		this.estimation = estimation;
		this.numberOfFiles = numberOfFiles;
		this.totalFileSize = totalFileSize;
		this.estimatedTransferSize = estimatedTransferSize;
		this.numberOfTransferredFiles = numberOfTransferredFiles;
		this.numberOfCreatedFiles = numberOfCreatedFiles;
		this.transferredFileSize = transferredFileSize;
		// These are used to control table info labels
		this.ongoing = false;
		this.successful = false;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getEstimation() {
		return estimation;
	}

	public void setEstimation(String estimation) {
		this.estimation = estimation;
	}

	public synchronized boolean isOngoing() {
		return ongoing;
	}

	public synchronized void setOngoing(boolean ongoing) {
		this.ongoing = ongoing;
	}

	public synchronized boolean isSuccessful() {
		return successful;
	}

	public synchronized void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(String numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public String getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(String totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public String getEstimatedTransferSize() {
		return estimatedTransferSize;
	}

	public void setEstimatedTransferSize(String estimatedTransferSize) {
		this.estimatedTransferSize = estimatedTransferSize;
	}

	public String getNumberOfTransferredFiles() {
		return numberOfTransferredFiles;
	}

	public void setNumberOfTransferredFiles(String numberOfTransferredFiles) {
		this.numberOfTransferredFiles = numberOfTransferredFiles;
	}

	public String getNumberOfCreatedFiles() {
		return numberOfCreatedFiles;
	}

	public void setNumberOfCreatedFiles(String numberOfCreatedFiles) {
		this.numberOfCreatedFiles = numberOfCreatedFiles;
	}

	public String getTransferredFileSize() {
		return transferredFileSize;
	}

	public void setTransferredFileSize(String transferredFileSize) {
		this.transferredFileSize = transferredFileSize;
	}

}
