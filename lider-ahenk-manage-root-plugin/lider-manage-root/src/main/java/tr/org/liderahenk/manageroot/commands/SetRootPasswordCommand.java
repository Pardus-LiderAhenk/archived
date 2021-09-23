package tr.org.liderahenk.manageroot.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.manageroot.entities.ManageRootEntity;

public class SetRootPasswordCommand implements ICommand, ITaskAwareCommand {

	private Logger logger = LoggerFactory.getLogger(SetRootPasswordCommand.class);
	
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
			 ManageRootEntity rootEntity = new ManageRootEntity();
					
					if(rootEntity.getId()==null){
						rootEntity.setCreateDate(new Date());
						rootEntity.setAgentDn(context.getRequest().getDnList().get(0));
						rootEntity.setOwner(req.getOwner());
						
						pluginDbService.save(rootEntity);
						parameterMap.put("rootEntity", rootEntity);
						
					}else{
						rootEntity.setModifyDate(new Date());
						pluginDbService.update(rootEntity);
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
	public String getCommandId() {
		return "SET_ROOT_PASSWORD";
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

	
	
	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {

		try {
			byte[] data = result.getResponseData();
			
			ObjectMapper mapper = new ObjectMapper();
			
			
			final Map<String, Object> responseData = mapper.readValue(data, 0, data.length,
					new TypeReference<HashMap<String, Object>>() {
					});
			
				ManageRootEntity entity = new ObjectMapper().readValue(mapper.writeValueAsString(responseData.get("rootEntity")),
						new TypeReference<ManageRootEntity>() {
				});
				
				if(entity!=null && entity.getId()!=null){
					
					if(result.getResponseCode()==StatusCode.TASK_ERROR){
						entity.setState("ERROR");
					}
					else
						entity.setState("OK");
					
					entity.setAgentId(result.getAgentId());
					pluginDbService.update(entity);
				}

		} catch (Exception e) {
			e.printStackTrace();
		}

	
	}

	
}
