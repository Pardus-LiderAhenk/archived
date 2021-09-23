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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgentProperty;
import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;

/**
 * Entity class for agent.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IAgent
 *
 */
@Entity
@Table(name = "C_AGENT")
public class AgentImpl implements IAgent {

	private static final long serialVersionUID = 3120888411065795936L;

	@Id
	@GeneratedValue
	@Column(name = "AGENT_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "JID", nullable = false, unique = true)
	private String jid; // XMPP JID = LDAP UID

	@Column(name = "IS_DELETED")
	private Boolean deleted;

	@Column(name = "DN", nullable = false, unique = true)
	private String dn;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "HOSTNAME", nullable = false)
	private String hostname;

	@Column(name = "IP_ADDRESSES", nullable = false)
	private String ipAddresses; // Comma-separated IP addresses

	@Column(name = "MAC_ADDRESSES", nullable = false)
	private String macAddresses; // Comma-separated MAC addresses

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<AgentPropertyImpl> properties = new HashSet<AgentPropertyImpl>(0); // bidirectional

	@OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
	private Set<UserSessionImpl> sessions = new HashSet<UserSessionImpl>(0); // bidirectional

	public AgentImpl() {
	}

	public AgentImpl(Long id, String jid, Boolean deleted, String dn, String password, String hostname,
			String ipAddresses, String macAddresses, Date createDate, Date modifyDate,
			Set<AgentPropertyImpl> properties, Set<UserSessionImpl> sessions) {
		super();
		this.id = id;
		this.jid = jid;
		this.deleted = deleted;
		this.dn = dn;
		this.password = password;
		this.hostname = hostname;
		this.ipAddresses = ipAddresses;
		this.macAddresses = macAddresses;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.properties = properties;
		this.sessions = sessions;
	}

	public AgentImpl(IAgent agent) {
		this.id = agent.getId();
		this.deleted = agent.getDeleted();
		this.jid = agent.getJid();
		this.dn = agent.getDn();
		this.password = agent.getPassword();
		this.hostname = agent.getHostname();
		this.ipAddresses = agent.getIpAddresses();
		this.macAddresses = agent.getMacAddresses();
		this.createDate = agent.getCreateDate();
		this.modifyDate = agent.getModifyDate();

		// Convert IAgentProperty to AgentPropertyImpl
		Set<? extends IAgentProperty> tmpProperties = agent.getProperties();
		if (tmpProperties != null) {
			for (IAgentProperty tmpProperty : tmpProperties) {
				addProperty(tmpProperty);
			}
		}

		// Convert IUserSession to UserSessionImpl
		Set<? extends IUserSession> tmpUserSessions = agent.getSessions();
		if (tmpUserSessions != null) {
			for (IUserSession tmpUserSession : tmpUserSessions) {
				addUserSession(tmpUserSession);
			}
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	@Override
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	@Override
	public String getMacAddresses() {
		return macAddresses;
	}

	public void setMacAddresses(String macAddresses) {
		this.macAddresses = macAddresses;
	}

	@Override
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public Set<AgentPropertyImpl> getProperties() {
		return properties;
	}

	public void setProperties(Set<AgentPropertyImpl> properties) {
		this.properties = properties;
	}

	@Override
	public void addProperty(IAgentProperty property) {
		if (properties == null) {
			properties = new HashSet<AgentPropertyImpl>(0);
		}
		AgentPropertyImpl propertyImpl = null;
		if (property instanceof AgentPropertyImpl) {
			propertyImpl = (AgentPropertyImpl) property;
		} else {
			propertyImpl = new AgentPropertyImpl(property);
		}
		if (propertyImpl.getAgent() != this) {
			propertyImpl.setAgent(this);
		}
		boolean found = false;
		for (AgentPropertyImpl tmp : properties) {
			if (tmp.equals(propertyImpl)) {
				tmp.setPropertyValue(propertyImpl.getPropertyValue());
				found = true;
				break;
			}
		}
		if (!found) {
			properties.add(propertyImpl);
		}
	}

	@Override
	public Set<UserSessionImpl> getSessions() {
		return sessions;
	}

	public void setSessions(Set<UserSessionImpl> sessions) {
		this.sessions = sessions;
	}

	@Override
	public void addUserSession(IUserSession userSession) {
		if (sessions == null) {
			sessions = new HashSet<UserSessionImpl>(0);
		}
		UserSessionImpl userSessionImpl = null;
		if (userSession instanceof UserSessionImpl) {
			userSessionImpl = (UserSessionImpl) userSession;
		} else {
			userSessionImpl = new UserSessionImpl(userSession);
		}
		if (userSessionImpl.getAgent() != this) {
			userSessionImpl.setAgent(this);
		}
		sessions.add(userSessionImpl);
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "AgentImpl [id=" + id + ", jid=" + jid + ", deleted=" + deleted + ", dn=" + dn + ", password=" + password
				+ ", hostname=" + hostname + ", ipAddresses=" + ipAddresses + ", macAddresses=" + macAddresses
				+ ", createDate=" + createDate + ", modifyDate=" + modifyDate + ", properties=" + properties
				+ ", sessions=" + sessions + "]";
	}

}
