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
package tr.org.liderahenk.lider.policymanager;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.constants.LiderConstants;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.IPolicyStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicyStatusSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;

/**
 * Default implementation for {@link IPolicyStatusSubscriber}. This class is
 * responsible for executing policies and handling policy status messages.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PolicyManagerImpl implements IPolicyStatusSubscriber {

	private static Logger logger = LoggerFactory.getLogger(PolicyManagerImpl.class);

	private ICommandDao commandDao;
	private IAgentDao agentDao;
	private IEntityFactory entityFactory;
	private EventAdmin eventAdmin;

	public void init() {
		logger.info("Initializing policy manager.");
	}
	
	// TODO execute policy method

	/**
	 * Triggered when a policy status message received. This method listens to
	 * agent responses and creates new command execution results accordingly.
	 * It also throws a policy status event in order to notify plugins about
	 * policy result.
	 * 
	 * @throws Exception
	 * @see tr.org.liderahenk.lider.core.api.messaging.messages.
	 *      IPolicyStatusMessage
	 */
	@Override
	public void messageReceived(IPolicyStatusMessage message) throws Exception {
		if (message != null) {
			logger.info("Policy manager received message from {}", message.getFrom());
			
			String mailSubject = null;
			String mailContent = null;
			
			try {
				
				
				if(message.getResponseData()!=null && message.getResponseData().get("mail_send")!=null)
				{
				Boolean mailSend = (Boolean) message.getResponseData().get("mail_send");
				mailSubject = (String) (mailSend != null && mailSend.booleanValue()
						? message.getResponseData().get("mail_subject") : null);
				mailContent = (String) (mailSend != null && mailSend.booleanValue()
						? message.getResponseData().get("mail_content") : null);
				}
					
			} catch (Exception e1) {
				logger.error(e1.getMessage(), e1);
			}

			// Find related agent
			List<? extends IAgent> agents = agentDao.findByProperty(null, "jid", message.getFrom().split("@")[0], 1);
			if (agents != null) {

				IAgent agent = agents.get(0);
				if (agent != null) {
					// Find related command execution.
					ICommandExecution commandExecution = commandDao.findExecution(message.getCommandExecutionId());

					// Create new command execution result
					ICommandExecutionResult result = entityFactory.createCommandExecutionResult(message,
							commandExecution, agent.getId(), mailSubject, mailContent);
					commandExecution.addCommandExecutionResult(result);

					try {
						// Save command execution with result
						commandDao.save(result);
						// Throw an event if the task processing finished
						if (StatusCode.getTaskEndingStates().contains(message.getResponseCode())) {
							Dictionary<String, Object> payload = new Hashtable<String, Object>();
							// Task status message
							payload.put("message", message);
							// Find who created the task
							payload.put("messageJID", commandExecution.getCommand().getCommandOwnerUid());
							eventAdmin.postEvent(new Event(LiderConstants.EVENTS.POLICY_STATUS_RECEIVED, payload));
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	/*
	 * Service setters
	 */

	/**
	 * 
	 * @param commandDao
	 */
	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
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

	/**
	 * 
	 * @param eventAdmin
	 */
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

}
