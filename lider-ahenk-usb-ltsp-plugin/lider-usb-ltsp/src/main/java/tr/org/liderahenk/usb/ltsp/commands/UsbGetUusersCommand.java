package tr.org.liderahenk.usb.ltsp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.usb.ltsp.plugininfo.PluginInfoImpl;

public class UsbGetUusersCommand implements ICommand, ITaskAwareCommand {

	private static final Logger logger = LoggerFactory.getLogger(UsbGetUusersCommand.class);

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private IPluginDbService dbService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
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
		return "GET_USERS";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
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
	
	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {
		try {
			byte[] data = result.getResponseData();
			
			final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
					new TypeReference<HashMap<String, Object>>() {
					});
			ObjectMapper mapper = new ObjectMapper();
			
			List<String> users = new ObjectMapper().readValue(mapper.writeValueAsString(responseData.get("users")),
						new TypeReference<List<String>>() {
				});
			
			if(users!=null){
				
				for (String user : users) {
					
					logger.info("Getting user from agent username : " +user);
					
//					HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
//					propertiesMap.put("username",user );
//					propertiesMap.put("agentId",result.getAgentId() );
//					
//					List<UsbFuseGroupResult> userFuseResult= dbService.findByProperties(UsbFuseGroupResult.class,propertiesMap,null, 1);
//					
//					
//					if(userFuseResult.size()==0){
//					
//						UsbFuseGroupResult usbFuseGroupResult= new UsbFuseGroupResult();
//						usbFuseGroupResult.setCreateDate(new Date());
//						usbFuseGroupResult.setAgentId(result.getAgentId());
//						usbFuseGroupResult.setUsername(user);
//								
//						dbService.save(usbFuseGroupResult);}
					}
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	

}
