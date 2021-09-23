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
package tr.org.liderahenk.lider.core.api.messaging;

import java.util.Date;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.FileServerConf;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IInstallPluginMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IPluginNotFoundMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationResponseMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IResponseAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IUpdateScheduledTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskNotification;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskStatusNotification;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;

/**
 * Factory interface to create {@link ILiderMessage}'s from various objects
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IMessageFactory {

	/**
	 * Create task execution message from provided task instance.
	 * 
	 * @param task
	 * @param jid
	 * @param fileServerConf
	 * @return
	 */
	IExecuteTaskMessage createExecuteTaskMessage(ITask task, String jid, FileServerConf fileServerConf);

	/**
	 * Create execute policies message from provided (user and machine) profile
	 * lists.
	 * 
	 * @param recipient
	 * @param username
	 * @param userPolicyProfiles
	 * @param userPolicyVersion
	 * @param userCommandExecutionId
	 * @param agentPolicyProfiles
	 * @param agentPolicyVersion
	 * @param agentCommandExecutionId
	 * @param fileServerConf
	 * @return
	 */
	IExecutePoliciesMessage createExecutePoliciesMessage(String recipient, String username,
			List<IProfile> userPolicyProfiles, String userPolicyVersion, Long userCommandExecutionId,
			Date userPolicyExpirationDate, List<IProfile> agentPolicyProfiles, String agentPolicyVersion,
			Long agentCommandExecutionId, Date agentPolicyExpirationDate, FileServerConf fileServerConf);

	/**
	 * Create plugin not found message for specified plugin name-version
	 * 
	 * @param recipient
	 * @param pluginName
	 * @param pluginVersion
	 * @return
	 */
	IPluginNotFoundMessage createPluginNotFoundMessage(String recipient, String pluginName, String pluginVersion);

	/**
	 * Create install plugin message for specified plugin name-version
	 * 
	 * @param recipient
	 * @param pluginName
	 * @param pluginVersion
	 * @param parameterMap
	 * @param protocol
	 * @return
	 */
	IInstallPluginMessage createInstallPluginMessage(String recipient, String pluginName, String pluginVersion,
			Map<String, Object> parameterMap, Protocol protocol);

	/**
	 * Create task notification that can be used to notify related Lider Console
	 * user.
	 * 
	 * @param recipient
	 * @param command
	 * @return
	 */
	ITaskNotification createTaskNotification(String recipient, ICommand command);

	/**
	 * Create task status notification that can be used to notify related
	 * plugins and Lider Console user.
	 * 
	 * @param recipient
	 * @param result
	 * @return
	 */
	ITaskStatusNotification createTaskStatusNotification(String recipient, ICommandExecutionResult result);

	/**
	 * Create agreement response message
	 * 
	 * @param from
	 * @param parameterMap
	 * @param protocol
	 * @return
	 */
	IResponseAgreementMessage createResponseAgreementMessage(String from, Map<String, Object> parameterMap,
			Protocol protocol);

	/**
	 * Create execute script message
	 * 
	 * @param recipient
	 * @param command
	 * @param fileServerConf
	 * @return
	 */
	IExecuteScriptMessage createExecuteScriptMessage(String recipient, String command, FileServerConf fileServerConf);

	/**
	 * 
	 * @param recipient
	 * @param status
	 * @param message
	 * @param agentDn
	 * @return
	 */
	IRegistrationResponseMessage createRegistrationResponseMessage(String recipient, StatusCode status, String message,
			String agentDn);

	/**
	 * 
	 * @param recipient
	 * @param taskId
	 * @param cronExpression
	 * @return
	 */
	IUpdateScheduledTaskMessage createUpdateScheduledTaskMessage(String recipient, Long taskId, String cronExpression);

}
