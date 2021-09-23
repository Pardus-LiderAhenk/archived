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
package tr.org.liderahenk.lider.router;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.exceptions.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandContextFactory;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.exceptions.TaskExecutionFailedException;

/**
 * Default implementation for {@link IServiceRouter}. ServiceRouterImpl handles
 * validation and execution phases. After command execution, if a task is
 * needed, request is delegated to the task manager.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ServiceRouterImpl implements IServiceRouter {

	private static Logger logger = LoggerFactory.getLogger(ServiceRouterImpl.class);

	private IServiceRegistry serviceRegistry;
	private IResponseFactory responseFactory;
	private ICommandContextFactory commandContextFactory;
	private ITaskManager taskManager;

	@Override
	public IRestResponse delegateRequest(ITaskRequest request, List<LdapEntry> entries)
			throws InvalidRequestException, TaskExecutionFailedException {

		// Try to find related ICommand instance.
		String key = serviceRegistry.buildKey(request.getPluginName(), request.getPluginVersion(),
				request.getCommandId());
		ICommand command = serviceRegistry.lookupCommand(key);
		if (null == command) {
			throw new InvalidRequestException(request);
		}
		logger.debug("ICommand found. Plugin name: {}, Plugin version: {}, Command ID: {}",
				new Object[] { command.getPluginName(), command.getPluginVersion(), command.getCommandId() });

		ICommandContext commandContext = commandContextFactory.create(request);

		// Validate! If validation fails, return error response.
		ICommandResult validationResult = command.validate(commandContext);
		if (CommandResultStatus.OK != validationResult.getStatus()) {
			return responseFactory.createResponse(validationResult);
		}

		// Execute command, if a task is needed, delegate request to the task
		// manager. Finally, return result response.
		ICommandResult commandResult = null;
		try {
			logger.info("Executing command: {}", command);
			commandResult = command.execute(commandContext);
			logger.info("Command executed successfully.");
			logger.debug("Command result: {}", commandResult);
		} catch (Throwable e1) { // use throwable instead of exception to catch
									// OutOfMemoryError as well.
			logger.error(e1.getMessage(), e1);
			List<String> messages = new ArrayList<String>();
			messages.add("Could not execute command: " + e1.getMessage());
			return responseFactory.createResponse(request, RestResponseStatus.ERROR, messages);
		}

		if (CommandResultStatus.OK == commandResult.getStatus()) {
			if (!command.executeOnAgent()) {
				logger.debug("{} does not require task, returning response.", command);
				return responseFactory.createResponse(commandResult);
			} else {
				logger.debug("{} requires task, delegating request to task manager.", command);
				try {
					logger.info("Delegating request to task manager.");
					taskManager.executeTask(request, entries);
				} catch (Exception e) {
					logger.error("Could not add task for request: ", e);
					List<String> messages = new ArrayList<String>();
					messages.add("Could not add task for request: " + e.getMessage());
					return responseFactory.createResponse(request, RestResponseStatus.ERROR, messages);
				}
				return responseFactory.createResponse(commandResult);
			}
		} else {
			return responseFactory.createResponse(commandResult);
		}

	}

	public void setServiceRegistry(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setCommandContextFactory(ICommandContextFactory commandContextFactory) {
		this.commandContextFactory = commandContextFactory;
	}

	public void setTaskManager(ITaskManager taskManager) {
		this.taskManager = taskManager;
	}

}
