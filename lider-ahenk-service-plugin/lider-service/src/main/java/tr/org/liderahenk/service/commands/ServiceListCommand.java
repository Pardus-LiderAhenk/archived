package tr.org.liderahenk.service.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.service.entities.ServiceListItem;

public class ServiceListCommand implements ICommand , ITaskAwareCommand{

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IPluginDbService pluginDbService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "SERVICE_LIST";
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

	
	// after service status changed update db for this service on releated agent..
	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {

		try {

			byte[] data = result.getResponseData();
			
			final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
					new TypeReference<HashMap<String, Object>>() {
					});

			ObjectMapper mapper = new ObjectMapper();
			
			List<ServiceListItem> services = new ObjectMapper().readValue(mapper.writeValueAsString(responseData.get("service_list")),
						new TypeReference<List<ServiceListItem>>() {
				});
			
			if(services!=null)
				for (ServiceListItem serviceListItem : services) {
					
					List<PropertyOrder> orders = new ArrayList<PropertyOrder>();
					orders.add(new PropertyOrder("createDate", OrderType.DESC));
					
					HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
					propertiesMap.put("id", serviceListItem.getId());
					
					List<ServiceListItem> serviceList= pluginDbService.findByProperties(ServiceListItem.class,propertiesMap,orders, 1);
					
					if(serviceList!=null && serviceList.size()>0){
						ServiceListItem service= serviceList.get(0);
						
						service.setServiceStatus(serviceListItem.getServiceStatus());
						
						pluginDbService.update(service);
					}
				}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public IPluginDbService getPluginDbService() {
		return pluginDbService;
	}

	public void setPluginDbService(IPluginDbService pluginDbService) {
		this.pluginDbService = pluginDbService;
	}

	public IPluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
}
