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
package tr.org.liderahenk.lider.messaging;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.FileServerConf;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IInstallPluginMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IPluginNotFoundMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationResponseMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IResponseAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IUpdateScheduledTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskNotification;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskStatusNotification;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.messaging.messages.ExecutePoliciesMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteScriptMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteTaskMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.InstallPluginMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.PluginNotFoundMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RegistrationResponseMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ResponseAgreementMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.UpdateScheduledTaskMessageImpl;
import tr.org.liderahenk.lider.messaging.notifications.TaskNotificationImpl;
import tr.org.liderahenk.lider.messaging.notifications.TaskStatusNotificationImpl;

/**
 * Default implementation for {@link IMessageFactory}. Responsible for creating
 * XMPP messages for agents and notification for Lider Console.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MessageFactoryImpl implements IMessageFactory {

	private static Logger logger = LoggerFactory.getLogger(MessageFactoryImpl.class);

	@Override
	public IExecuteTaskMessage createExecuteTaskMessage(ITask task, String jid, FileServerConf fileServerConf) {
		String taskJsonString = null;
		try {
			taskJsonString = task.toJson();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ExecuteTaskMessageImpl(taskJsonString, jid, new Date(), fileServerConf);
	}

	@Override
	public IExecuteScriptMessage createExecuteScriptMessage(String recipient, String command,
			FileServerConf fileServerConf) {
		return new ExecuteScriptMessageImpl(command, recipient, new Date(), fileServerConf);
	}

	@Override
	public IRegistrationResponseMessage createRegistrationResponseMessage(String recipient, StatusCode status,
			String message, String agentDn) {
		return new RegistrationResponseMessageImpl(status, message, agentDn, recipient, new Date());
	}

	@Override
	public IExecutePoliciesMessage createExecutePoliciesMessage(String recipient, String username,
			List<IProfile> userPolicyProfiles, String userPolicyVersion, Long userCommandExecutionId,
			Date userPolicyExpirationDate, List<IProfile> agentPolicyProfiles, String agentPolicyVersion, 
			Long agentCommandExecutionId, Date agentPolicyExpirationDate, FileServerConf fileServerConf) {
		return new ExecutePoliciesMessageImpl(recipient, username, userPolicyProfiles, userPolicyVersion,
				userCommandExecutionId, userPolicyExpirationDate, agentPolicyProfiles, agentPolicyVersion, 
				agentCommandExecutionId, agentPolicyExpirationDate, new Date(), fileServerConf);
	}

	@Override
	public IPluginNotFoundMessage createPluginNotFoundMessage(String recipient, String pluginName,
			String pluginVersion) {
		return new PluginNotFoundMessageImpl(recipient, pluginName, pluginVersion, new Date());
	}

	@Override
	public IInstallPluginMessage createInstallPluginMessage(String recipient, String pluginName, String pluginVersion,
			Map<String, Object> parameterMap, Protocol protocol) {
		return new InstallPluginMessageImpl(recipient, pluginName, pluginVersion, parameterMap, protocol, new Date());
	}

	@Override
	public ITaskNotification createTaskNotification(String recipient, ICommand command) {
		return new TaskNotificationImpl(recipient, command, new Date());
	}

	@Override
	public ITaskStatusNotification createTaskStatusNotification(String recipient, ICommandExecutionResult result) {
		IPlugin p = result.getCommandExecution().getCommand().getTask().getPlugin();
		return new TaskStatusNotificationImpl(recipient, p.getName(), p.getVersion(),
				result.getCommandExecution().getCommand().getTask().getCommandClsId(), result.getCommandExecution(),
				result, new Date());
	}

	@Override
	public IResponseAgreementMessage createResponseAgreementMessage(String from, Map<String, Object> parameterMap,
			Protocol protocol) {
		return new ResponseAgreementMessageImpl(from, parameterMap, protocol, new Date());
	}

	@Override
	public IUpdateScheduledTaskMessage createUpdateScheduledTaskMessage(String recipient, Long taskId,
			String cronExpression) {
		return new UpdateScheduledTaskMessageImpl(recipient, taskId, cronExpression, new Date());
	}

}
