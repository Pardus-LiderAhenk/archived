package tr.org.liderahenk.service.entities;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "P_SERVICE")
public class ServiceListItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue
	@Column(name = "SERVICE_ID")
	private Long id;
	
//	@Column(name = "OWNER")
//	private String owner;
	
	
//	@Column(name = "AGENT_ID")
//	private Long agentId;
	
	@Column(name = "AGENT_DN")
	private String agentDn;

//	@Column(name = "AGENT_UID")
//	private String agentUid;
	
	@Column(name = "SERVICE_NAME")
	private String serviceName;
	
	@Column(name = "STATUS")
	private String serviceStatus;
	
//	@Column(name = "TASK_ID")
//	private Long taskId;
	
	@Column(name = "DELETED")
	private boolean deleted = false;
	
	@Column(name = "IS_MONITORING")
	private boolean isServiceMonitoring;
	
	@Column(name = "DESIRED_SERVICE_STATUS")
	private String desiredServiceStatus;
	
	@Column(name = "DESIRED_START_AUTO")
	private String desiredStartAuto;
	
	
	
	@Transient
	private boolean updated = false;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;
	
	@Column(name = "START_AUTO")
	private String startAuto;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public String getOwner() {
//		return owner;
//	}
//
//	public void setOwner(String owner) {
//		this.owner = owner;
//	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getStartAuto() {
		return startAuto;
	}

	public void setStartAuto(String startAuto) {
		this.startAuto = startAuto;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isServiceMonitoring() {
		return isServiceMonitoring;
	}

	public void setServiceMonitoring(boolean isServiceMonitoring) {
		this.isServiceMonitoring = isServiceMonitoring;
	}

//	public Long getAgentId() {
//		return agentId;
//	}
//
//	public void setAgentId(Long agentId) {
//		this.agentId = agentId;
//	}

	public String getDesiredServiceStatus() {
		return desiredServiceStatus;
	}

	public void setDesiredServiceStatus(String desiredServiceStatus) {
		this.desiredServiceStatus = desiredServiceStatus;
	}

	public String getDesiredStartAuto() {
		return desiredStartAuto;
	}

	public void setDesiredStartAuto(String desiredStartAuto) {
		this.desiredStartAuto = desiredStartAuto;
	}

	public String getAgentDn() {
		return agentDn;
	}

	public void setAgentDn(String agentDn) {
		this.agentDn = agentDn;
	}

//	public Long getTaskId() {
//		return taskId;
//	}
//
//	public void setTaskId(Long taskId) {
//		this.taskId = taskId;
//	}
//
//	public String getAgentUid() {
//		return agentUid;
//	}
//
//	public void setAgentUid(String agentUid) {
//		this.agentUid = agentUid;
//	}
	
}
