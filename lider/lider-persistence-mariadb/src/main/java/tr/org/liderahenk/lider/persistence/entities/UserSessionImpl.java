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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;

/**
 * Entity class for user login/logout events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession
 *
 */
@JsonIgnoreProperties({ "agent" })
@Entity
@Table(name = "C_AGENT_USER_SESSION")
public class UserSessionImpl implements IUserSession {

	private static final long serialVersionUID = -9102362700808969139L;

	@Id
	@GeneratedValue
	@Column(name = "USER_SESSION_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AGENT_ID", nullable = false)
	private AgentImpl agent; // bidirectional

	@Column(name = "USERNAME", nullable = false)
	private String username;
	
	/**
	 * for user thin client 
	 */
	@Column(name = "USERIP")
	private String userIp;

	@Column(name = "SESSION_EVENT", nullable = false, length = 1)
	private Integer sessionEvent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public UserSessionImpl() {
	}

	public UserSessionImpl(Long id, AgentImpl agent, String username, String userIp, SessionEvent sessionEvent, Date createDate) {
		super();
		this.id = id;
		this.agent = agent;
		this.username = username;
		this.userIp=userIp;
		setSessionEvent(sessionEvent);
		this.createDate = createDate;
	}

	public UserSessionImpl(IUserSession userSession) {
		this.id = userSession.getId();
		this.username = userSession.getUsername();
		setSessionEvent(userSession.getSessionEvent());
		this.createDate = userSession.getCreateDate();
		if (userSession.getAgent() instanceof AgentImpl) {
			this.agent = (AgentImpl) userSession.getAgent();
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
	public AgentImpl getAgent() {
		return agent;
	}

	public void setAgent(AgentImpl agent) {
		this.agent = agent;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public SessionEvent getSessionEvent() {
		return SessionEvent.getType(sessionEvent);
	}

	public void setSessionEvent(SessionEvent sessionEvent) {
		if (sessionEvent == null) {
			this.sessionEvent = null;
		} else {
			this.sessionEvent = sessionEvent.getId();
		}
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "UserSessionImpl [id=" + id + ", username=" + username + ", sessionEvent=" + sessionEvent
				+ ", createDate=" + createDate + "]";
	}

}
