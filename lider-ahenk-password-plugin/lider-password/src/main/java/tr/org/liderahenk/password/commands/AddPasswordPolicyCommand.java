package tr.org.liderahenk.password.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class AddPasswordPolicyCommand implements ICommand {

	private static final Logger logger = LoggerFactory.getLogger(AddPasswordPolicyCommand.class);
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private ILDAPService ldapService;
	private IOperationLogService logService;
	private IConfigurationService configurationService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		
		ITaskRequest req = context.getRequest();
		
		List<String> dnList=  req.getDnList();
		
		Map<String, Object> resultMap = req.getParameterMap();
		
		String passwordPolicyDn = (String) resultMap.get("password_policy");
		
		try {
			
			for (String entryDn : dnList) {
				//ldapService.updateEntryRemoveAttribute(entryDn, "pwdPolicySubentry");
				
//				ldapService.updateEntryRemoveAttributeWithValue(entryDn, "pwdPolicySubentry", passwordPolicyDn);
				ldapService.updateEntry(entryDn, "pwdPolicySubentry", passwordPolicyDn);
			}
			
			
		} catch (LdapException e1) {
			e1.printStackTrace();
			return resultFactory.create(CommandResultStatus.ERROR, new ArrayList<String>(), this);
		}
		
		
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "ADD_PASSWORD_POLICY";
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

	public ILDAPService getLdapService() {
		return ldapService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public IOperationLogService getLogService() {
		return logService;
	}

	public void setLogService(IOperationLogService logService) {
		this.logService = logService;
	}

	public IConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public ICommandResultFactory getResultFactory() {
		return resultFactory;
	}

	public IPluginInfo getPluginInfo() {
		return pluginInfo;
	}

}
