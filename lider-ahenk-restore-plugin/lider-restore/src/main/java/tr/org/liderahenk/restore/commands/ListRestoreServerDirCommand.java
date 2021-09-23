package tr.org.liderahenk.restore.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.entities.BackupServerConfig;
import tr.org.liderahenk.restore.utils.SSHManager;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.restore.plugininfo.PluginInfoImpl;

public class ListRestoreServerDirCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(ListRestoreServerDirCommand.class);

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private IPluginDbService dbService;

	private static final String FIND_CHILD_DIRECTORIES_FOR_RESTORE = "find {0} -maxdepth 1 -print0 | tr '\\0' ','";

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> parameterMap = context.getRequest().getParameterMap();
		List<BackupServerConfig> config = dbService.findAll(BackupServerConfig.class);
		if (config != null && !config.isEmpty()) {
			BackupServerConfig serverConfig = config.get(0);
			logger.info("Found backup server config. Config: {}.", serverConfig);
			SSHManager ssh = new SSHManager(serverConfig.getDestHost(), serverConfig.getUsername(),
					serverConfig.getPassword(), serverConfig.getDestPort(), null, null);
			ssh.connect();
			String result = ssh.execCommand("find {0} -maxdepth 1 -print0 | tr '\\0' ','",
					new Object[] { parameterMap.get("TARGET_PATH").toString() });
			if (result != null) {
				resultMap.put("CHILD_DIRS", result);
			}
			ssh.disconnect();
		}
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	@Override
	public String getCommandId() {
		return "LIST_RESTORE_SERVER_DIR";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(PluginInfoImpl pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setDbService(IPluginDbService dbService) {
		this.dbService = dbService;
	}

}
