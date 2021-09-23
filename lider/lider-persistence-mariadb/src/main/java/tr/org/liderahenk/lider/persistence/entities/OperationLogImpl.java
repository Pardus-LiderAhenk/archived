/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

/**
 * Entity class for IOperationLog objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog
 *
 */
@Entity
@Table(name = "C_OPERATION_LOG")
public class OperationLogImpl implements IOperationLog {

	private static final long serialVersionUID = -241241606291513291L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "CRUD_TYPE", length = 1)
	private Integer crudType;

	@Column(name = "TASK_ID")
	private Long taskId;

	@Column(name = "POLICY_ID")
	private Long policyId;

	@Column(name = "PROFILE_ID")
	private Long profileId;

	@Column(name = "LOG_MESSAGE", nullable = false)
	private String logMessage;

	@Column(name = "REQUEST_DATA")
	private byte[] requestData;

	@Column(name = "REQUEST_IP")
	private String requestIp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public OperationLogImpl() {
	}

	public OperationLogImpl(Long id, String userId, CrudType crudType, Long taskId, Long policyId, Long profileId,
			String logMessage, byte[] requestData, String requestIp, Date createDate) {
		this.id = id;
		this.userId = userId;
		setCrudType(crudType);
		this.taskId = taskId;
		this.policyId = policyId;
		this.profileId = profileId;
		this.logMessage = logMessage;
		this.requestData = requestData;
		this.requestIp = requestIp;
		this.createDate = createDate;
	}

	public OperationLogImpl(IOperationLog log) {
		this.id = log.getId();
		this.userId = log.getUserId();
		setCrudType(log.getCrudType());
		this.taskId = log.getTaskId();
		this.policyId = log.getPolicyId();
		this.profileId = log.getProfileId();
		this.logMessage = log.getLogMessage();
		this.requestData = log.getRequestData();
		this.requestIp = log.getRequestIp();
		this.createDate = log.getCreateDate();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public CrudType getCrudType() {
		return CrudType.getType(crudType);
	}

	public void setCrudType(CrudType crudType) {
		if (crudType == null) {
			this.crudType = null;
		} else {
			this.crudType = crudType.getId();
		}
	}

	@Override
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Override
	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}

	@Override
	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	@Override
	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	@Override
	public byte[] getRequestData() {
		return requestData;
	}

	public void setRequestData(byte[] requestData) {
		this.requestData = requestData;
	}

	@Override
	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
