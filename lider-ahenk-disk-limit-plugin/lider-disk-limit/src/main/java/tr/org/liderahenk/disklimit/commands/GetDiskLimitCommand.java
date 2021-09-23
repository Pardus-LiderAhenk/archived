package tr.org.liderahenk.disklimit.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disklimit.entities.DiskUsageEntity;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class GetDiskLimitCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(GetDiskLimitCommand.class);

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private IPluginDbService pluginDbService;


	@Override
	public ICommandResult execute(ICommandContext context) {

		List<String> resultMessages;
		Map<String, Object> resultMap;
		try {
			ITaskRequest req = context.getRequest();
			
			
			Map<String, Object> properties= new HashMap<String, Object>();
			properties.put("owner", req.getOwner());
			properties.put("agentDn", req.getDnList().get(0));
			properties.put("deleted", false);
			
			List<DiskUsageEntity> diskUsage=pluginDbService.findByProperties(DiskUsageEntity.class, properties, null, null);
			
			resultMessages = new ArrayList<String>();
			resultMessages.add("İşlem Başarılı");
			
			resultMap = new HashMap<String, Object>();
			
			resultMap.put("diskUsageList", diskUsage);
		} catch (Exception e) {
			
			e.printStackTrace();
			return resultFactory.create(CommandResultStatus.ERROR, new ArrayList<String>(), this);
			
		}
		
		return resultFactory.create(CommandResultStatus.OK, resultMessages, this, resultMap);
	
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "GET_DISK_LIMIT";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
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

	public void setLogService(IOperationLogService logService) {
		this.logService = logService;
	}

	public void setPluginDbService(IPluginDbService pluginDbService) {
		this.pluginDbService = pluginDbService;
	}

	public ICommandResultFactory getResultFactory() {
		return resultFactory;
	}

	public IPluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public IOperationLogService getLogService() {
		return logService;
	}

	public IPluginDbService getPluginDbService() {
		return pluginDbService;
	}
	

}
