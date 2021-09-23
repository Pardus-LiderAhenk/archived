package tr.org.liderahenk.backup.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.entities.BackupServerConfig;
import tr.org.liderahenk.backup.plugininfo.PluginInfoImpl;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class SaveBackupServerConfigCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(SaveBackupServerConfigCommand.class);

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private IPluginDbService dbService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		ITaskRequest request = context.getRequest();
		ObjectMapper mapper = new ObjectMapper();
		BackupServerConfig config = mapper.readValue(
				mapper.writeValueAsBytes(request.getParameterMap().get("BACKUP_SERVER_CONFIG")),
				BackupServerConfig.class);
		if (config.getId() != null) {
			dbService.update(config);
		} else {
			dbService.save(config);
		}
		logger.info("Saved backup server config with ID: {}", config.getId());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("BACKUP_SERVER_CONFIG", new ObjectMapper().writeValueAsString(config));
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
		return "SAVE_BACKUP_SERVER_CONFIG";
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
