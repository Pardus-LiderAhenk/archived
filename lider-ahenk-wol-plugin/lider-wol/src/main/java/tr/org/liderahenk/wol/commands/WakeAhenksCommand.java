package tr.org.liderahenk.wol.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.wol.plugininfo.PluginInfoImpl;

public class WakeAhenksCommand implements ICommand {
	
	private static Logger logger = LoggerFactory.getLogger(WakeAhenksCommand.class);
	
	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	
	private IAgentDao agentDao;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		
		List<String> dnList = context.getRequest().getDnList();
		
		for (String dn : dnList) {
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("dn", dn);
			
			List<? extends IAgent> agents = agentDao.findByProperties(IAgent.class, propertiesMap, null, 1);
			IAgent agent = agents.get(0);
			
			String[] macAddresses = agent.getMacAddresses().replace("'", "").split(",");
			
			for (String mac : macAddresses) {
				try {
					String command = "wakeonlan " + mac;
					logger.info("Wake-on-LAN command: " + command);
					Process process = Runtime.getRuntime().exec(command);
					int exitValue = process.waitFor();
					if (exitValue != 0) {
						logger.error("Failed to execute command: " + command);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
		
		ICommandResult commandResult = resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
		return commandResult;
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "WAKE-AHENK-MACHINES";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	public void setPluginInfo(PluginInfoImpl pluginInfoImpl) {
		this.pluginInfo = pluginInfoImpl;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}
	
}
