package tr.org.liderahenk.ldap.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class AddOuCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(AddOuCommand.class);
	
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private ILDAPService ldapService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		
		Map<String, Object> params= context.getRequest().getParameterMap();
		
		String dn= (String) params.get("dn");
		String ouName= (String) params.get("ou_name");
		String desc= (String) params.get("desc");
		
		try {
			
			Map<String, String[]> attributes = new HashMap<String, String[]>();
			attributes.put("objectClass", new String[] { "top", "organizationalUnit" , "pardusLider"});
			attributes.put("ou", new String[] { ouName });
			if(!"".equals(desc))
			attributes.put("description", new String[] { desc });
			
			dn="ou="+ouName+","+dn;
			
			ldapService.addEntry(dn, attributes);
			
			logger.info("ou created successfully ou =");
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}
	
	
	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "ADD_OU";
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


	public ILDAPService getLdapService() {
		return ldapService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
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
	
	
}
