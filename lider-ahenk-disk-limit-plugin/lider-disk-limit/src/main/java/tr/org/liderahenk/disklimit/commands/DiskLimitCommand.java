package tr.org.liderahenk.disklimit.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disklimit.entities.DiskUsageEntity;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class DiskLimitCommand implements ICommand, ITaskAwareCommand {

	private Logger logger = LoggerFactory.getLogger(DiskLimitCommand.class);

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private IPluginDbService pluginDbService;


	@Override
	public ICommandResult execute(ICommandContext context) {

		 ITaskRequest req = context.getRequest();
			
		 Map<String, Object> parameterMap = req.getParameterMap();
		 
		 ObjectMapper mapper = new ObjectMapper();
		 mapper.configure(
				    DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		 try {
			 DiskUsageEntity diskUsage = mapper.readValue(mapper.writeValueAsString(parameterMap.get("diskUsageEntity")),
						new TypeReference<DiskUsageEntity>() {
				});
			
			if(diskUsage!=null){
					
					if(diskUsage.getId()==null){
						diskUsage.setCreateDate(new Date());
						diskUsage.setAgentDn(context.getRequest().getDnList().get(0));
						diskUsage.setOwner(req.getOwner());
						
						pluginDbService.save(diskUsage);
						parameterMap.put("diskUsageEntity", diskUsage);
						
					}else{
						diskUsage.setModifyDate(new Date());
						pluginDbService.update(diskUsage);
					}
					
					
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return resultFactory.create(CommandResultStatus.ERROR, new ArrayList<String>(), this);
		} 
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {

		try {
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("name", pluginInfo.getPluginName());
			propertiesMap.put("version", pluginInfo.getPluginVersion());


			byte[] data = result.getResponseData();
			
			ObjectMapper mapper = new ObjectMapper();
			
			final Map<String, Object> responseData = mapper.readValue(data, 0, data.length,
					new TypeReference<HashMap<String, Object>>() {
					});
			
			//if(responseData.get("mail_send")!=null && (Boolean) responseData.get("mail_send")){
				
				DiskUsageEntity diskUsageEntity = new ObjectMapper().readValue(mapper.writeValueAsString(responseData.get("disk_limit")),
						new TypeReference<DiskUsageEntity>() {
				});
				
				
				if(diskUsageEntity!=null && diskUsageEntity.getId()!=null){
					
					diskUsageEntity.setAgentId(result.getAgentId());
					pluginDbService.update(diskUsageEntity);
				}

		//	}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "SET_DISK_LIMIT";
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
