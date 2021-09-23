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
package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.FileServerConf;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

/**
 * Default implementation for {@link IExecutePoliciesMessage}. This message is
 * sent <b>from Lider to agent</b> in order to execute specified policies. As a
 * response {@link PolicyStatusMessageImpl} will be returned.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.messaging.messages.PolicyStatusMessageImpl
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class ExecutePoliciesMessageImpl implements IExecutePoliciesMessage {

	private static final long serialVersionUID = 8283628510292186821L;

	private LiderMessageType type = LiderMessageType.EXECUTE_POLICY;

	private String recipient;

	private String username;

	private List<IProfile> userPolicyProfiles;

	private String userPolicyVersion;

	private Long userCommandExecutionId;

	private Date userPolicyExpirationDate;

	private List<IProfile> agentPolicyProfiles;

	private String agentPolicyVersion;

	private Long agentCommandExecutionId;

	private Date agentPolicyExpirationDate;

	private Date timestamp;

	private FileServerConf fileServerConf;

	public ExecutePoliciesMessageImpl(String recipient, String username, List<IProfile> userPolicyProfiles,
			String userPolicyVersion, Long userCommandExecutionId, Date userPolicyExpirationDate, List<IProfile> agentPolicyProfiles,
			String agentPolicyVersion, Long agentCommandExecutionId, Date agentPolicyExpirationDate, Date timestamp, FileServerConf fileServerConf) {
		this.recipient = recipient;
		this.username = username;
		this.userPolicyProfiles = userPolicyProfiles;
		this.userPolicyVersion = userPolicyVersion;
		this.userPolicyExpirationDate = userPolicyExpirationDate;
		this.userCommandExecutionId = userCommandExecutionId;
		this.agentPolicyProfiles = agentPolicyProfiles;
		this.agentPolicyVersion = agentPolicyVersion;
		this.agentCommandExecutionId = agentCommandExecutionId;
		this.agentPolicyExpirationDate = agentPolicyExpirationDate;
		this.timestamp = timestamp;
		this.fileServerConf = fileServerConf;
	}

	@Override
	public LiderMessageType getType() {
		return type;
	}

	public void setType(LiderMessageType type) {
		this.type = type;
	}

	@Override
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public List<IProfile> getUserPolicyProfiles() {
		return userPolicyProfiles;
	}

	public void setUserPolicyProfiles(List<IProfile> userPolicyProfiles) {
		this.userPolicyProfiles = userPolicyProfiles;
	}

	@Override
	public String getUserPolicyVersion() {
		return userPolicyVersion;
	}

	public void setUserPolicyVersion(String userPolicyVersion) {
		this.userPolicyVersion = userPolicyVersion;
	}

	@Override
	public Long getUserCommandExecutionId() {
		return userCommandExecutionId;
	}

	public void setUserCommandExecutionId(Long userCommandExecutionId) {
		this.userCommandExecutionId = userCommandExecutionId;
	}

	@Override
	public List<IProfile> getAgentPolicyProfiles() {
		return agentPolicyProfiles;
	}

	public void setAgentPolicyProfiles(List<IProfile> agentPolicyProfiles) {
		this.agentPolicyProfiles = agentPolicyProfiles;
	}

	@Override
	public Long getAgentCommandExecutionId() {
		return agentCommandExecutionId;
	}

	public void setAgentCommandExecutionId(Long agentCommandExecutionId) {
		this.agentCommandExecutionId = agentCommandExecutionId;
	}

	@Override
	public String getAgentPolicyVersion() {
		return agentPolicyVersion;
	}

	public void setAgentPolicyVersion(String agentPolicyVersion) {
		this.agentPolicyVersion = agentPolicyVersion;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public FileServerConf getFileServerConf() {
		return fileServerConf;
	}

	public void setFileServerConf(FileServerConf fileServerConf) {
		this.fileServerConf = fileServerConf;
	}

	@Override
	public Date getUserPolicyExpirationDate() {
		return userPolicyExpirationDate;
	}

	public void setUserPolicyExpirationDate(Date userPolicyExpirationDate) {
		this.userPolicyExpirationDate = userPolicyExpirationDate;
	}

	@Override
	public Date getAgentPolicyExpirationDate() {
		return agentPolicyExpirationDate;
	}

	public void setAgentPolicyExpirationDate(Date agentPolicyExpirationDate) {
		this.agentPolicyExpirationDate = agentPolicyExpirationDate;
	}

}
