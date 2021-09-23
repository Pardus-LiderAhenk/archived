package tr.org.liderahenk.service.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.service.entities.ServiceListItem;

public class DeleteServiceCommand implements ICommand{
	
	
	private static Logger logger = LoggerFactory.getLogger(ServiceCommand.class);
	
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IPluginDbService pluginDbService;
	
	

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		
		logger.info("Delete Service executing");
		
		 ITaskRequest req = context.getRequest();
		 
		 Map<String, Object> parameterMap = req.getParameterMap();
		 
		 ObjectMapper mapper = new ObjectMapper();
		 mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		 try {
			List<ServiceListItem> serviceList = mapper.readValue(mapper.writeValueAsString(parameterMap.get("deletedServices")),
						new TypeReference<List<ServiceListItem>>() {
				});
			
			if(serviceList!=null && serviceList.size()>0){
				for (ServiceListItem serviceListItem : serviceList) {
					if(serviceListItem.getId()!=null){
						serviceListItem.setModifyDate(new Date());
						pluginDbService.update(serviceListItem);
					}
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			return resultFactory.create(CommandResultStatus.ERROR, new ArrayList<String>(), this);
		} 
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
		
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
		return "DELETE_SERVICES";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public ICommandResultFactory getResultFactory() {
		return resultFactory;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public IPluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(IPluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public IPluginDbService getPluginDbService() {
		return pluginDbService;
	}

	public void setPluginDbService(IPluginDbService pluginDbService) {
		this.pluginDbService = pluginDbService;
	}

}
