package tr.org.liderahenk.usb.ltsp.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import tr.org.liderahenk.usb.ltsp.enums.StatusCode;

public class UsbFuseGroupResult implements Serializable {

	private static final long serialVersionUID = 749394306065638349L;

	private Long id;

	private String username;

	private String uid;

	private Integer statusCode;
	
	private Long agentId;
	
	private String agentDn;
	
	private boolean deleted = false;

	private Date createDate;

	private Date endDate;

	public UsbFuseGroupResult() {
	}

	public UsbFuseGroupResult(Long id, String username, String uid, Integer statusCode, Date createDate, Date endDate) {
		super();
		this.id = id;
		this.username = username;
		this.uid = uid;
		this.statusCode = statusCode;
		this.createDate = createDate;
		this.endDate = endDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public StatusCode getStatusCode() {
		return StatusCode.getType(statusCode);
	}
	
	public void setStatusCode(StatusCode statusCode) {
		if (statusCode == null) {
			this.statusCode = null;
		} else {
			this.statusCode = statusCode.getId();
		}
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getAgentDn() {
		return agentDn;
	}

	public void setAgentDn(String agentDn) {
		this.agentDn = agentDn;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	@JsonIgnore
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

}
