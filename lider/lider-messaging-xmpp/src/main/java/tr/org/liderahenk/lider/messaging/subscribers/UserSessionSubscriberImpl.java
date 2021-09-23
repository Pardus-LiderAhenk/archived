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
package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IUserSessionMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IUserSessionSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;

/**
 * <p>
 * Provides default user login/logout event handler in case no other bundle
 * provides its user session subscriber.
 * </p>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.IUserSessionSubscriber
 * @see tr.org.liderahenk.lider.core.api.messaging.IUserSessionMessage
 *
 */
public class UserSessionSubscriberImpl implements IUserSessionSubscriber {

	private static Logger logger = LoggerFactory.getLogger(UserSessionSubscriberImpl.class);

	private IAgentDao agentDao;
	private IEntityFactory entityFactory;

	@Override
	public void messageReceived(IUserSessionMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Find related agent record
		List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
		IAgent agent = agentList != null && !agentList.isEmpty() ? agentList.get(0) : null;

		if (agent != null) {
			// Add new user session info
			IUserSession userSession = entityFactory.createUserSession(message.getUsername(), message.getUserIp(),getSessionEvent(message.getType()));
			agent.addUserSession(userSession);
			if (message.getType() == AgentMessageType.LOGIN
					&& (message.getIpAddresses() == null || message.getIpAddresses().isEmpty())) {
				logger.warn("Couldn't find IP addresses of the agent with JID: {}", uid);
			}
			// Merge records
			agentDao.update(agent, message.getIpAddresses());
			logger.debug("Added user session to the agent: {}", agent);
		} else {
			logger.warn("Couldn't find the agent with JID: {}", uid);
		}
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private SessionEvent getSessionEvent(AgentMessageType type) {
		switch (type) {
		case LOGIN:
			return SessionEvent.LOGIN;
		case LOGOUT:
			return SessionEvent.LOGOUT;
		default:
			return null;
		}
	}

	/**
	 * 
	 * @param agentDao
	 */
	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
