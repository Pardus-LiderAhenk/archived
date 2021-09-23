package tr.org.liderahenk.packagemanager.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class CheckPackageCommand implements ICommand, ITaskAwareCommand {

	private static final Logger logger = LoggerFactory.getLogger(CheckPackageCommand.class);

	// These strings are subject to change (check related strings in
	// check_package.py):
	private static final String NOT_INSTALLED = "Paket yüklü değil";

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IAgentDao agentDao;
	private IEntityFactory entityFactory;

	@Override
	public ICommandResult execute(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {
		try {
			Map<String, Object> parameterMap = result.getCommandExecution().getCommand().getTask().getParameterMap();
			Map<String, Object> responseData = new ObjectMapper().readValue(result.getResponseData(), 0,
					result.getResponseData().length, new TypeReference<HashMap<String, Object>>() {
					});
			String packageName = parameterMap.get("packageName").toString();
			String packageVersion = responseData.get("version").toString();
			String chResult = responseData.get("res").toString();
			if (!NOT_INSTALLED.equalsIgnoreCase(chResult)) {
				logger.info("Package {}:{} found, adding package property to database.",
						new Object[] { packageName, packageVersion });
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(packageName, packageVersion);
				// Update agent - add new property for the package found
				List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid",
						result.getCommandExecution().getUid(), 1);
				IAgent agent = agents != null && !agents.isEmpty() ? agents.get(0) : null;
				agent = entityFactory.createAgent(agent, null, agent.getPassword(), agent.getHostname(),
						agent.getIpAddresses(), agent.getMacAddresses(), data);
				agentDao.update(agent);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public String getCommandId() {
		return "CHECK_PACKAGE";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(IPluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
