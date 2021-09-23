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

public class AddUserCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(AddUserCommand.class);
	
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private ILDAPService ldapService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		
		Map<String, Object> params= context.getRequest().getParameterMap();
		
	
		String dn= (String) params.get("dn");
		String cn= (String) params.get("cn");
		String gidNumber= (String) params.get("gidNumber");
		String sn= (String) params.get("sn");
		String uid= (String) params.get("uid");
		int randomInt = (int)(1000000.0 * Math.random());
		
		String uidNumber= Integer.toString(randomInt);
		
	//	String uidNumber= (String) params.get("uidNumber");
		String password= (String) params.get("password");
		String home="/home/"+uid;
		
		
		try {
			
			Map<String, String[]> attributes = new HashMap<String, String[]>();
			attributes.put("objectClass", new String[] { "top", "posixAccount",
					"person","pardusLider","pardusAccount","organizationalPerson","inetOrgPerson"});
			attributes.put("cn", new String[] { cn });
			attributes.put("gidNumber", new String[] { gidNumber });
			attributes.put("homeDirectory", new String[] { home });
			attributes.put("sn", new String[] { sn });
			attributes.put("uid", new String[] { uid });
			attributes.put("uidNumber", new String[] { uidNumber });
			attributes.put("loginShell", new String[] { "/bin/bash" });
			attributes.put("userPassword", new String[] { password });
			
			dn="uid="+uid+","+dn;
			
			ldapService.addEntry(dn, attributes);
			
			logger.info("User created successfully RDN ="+dn);
			
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
		return "ADD_USER";
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
