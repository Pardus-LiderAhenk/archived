package tr.org.liderahenk.password.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class PasswordCommand implements ICommand {

	private static final Logger logger = LoggerFactory.getLogger(PasswordCommand.class);
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private ILDAPService ldapService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		ITaskRequest req = context.getRequest();
		Map<String, Object> parameterMap = req.getParameterMap();
		ArrayList<String> messages = new ArrayList<String>();
		if (parameterMap.size() > 0 && parameterMap.containsKey("password")) {
			List<String> dnList = req.getDnList();
			for (String dn : dnList) {
				try {
					ldapService.updateEntry(dn, "userPassword", parameterMap.get("password").toString());
				} catch (LdapException e) {
					logger.error(e.getMessage(), e);
					// TODO i18n - emre
					messages.add("dn: " + dn + " LDAP kullanıcı şifresi değiştirilemedi...\r\n");
				}
			}
		}

		if (messages.isEmpty()) {
			return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
		}
		return resultFactory.create(CommandResultStatus.ERROR, new ArrayList<String>(), this);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "CHANGE_LDAP_PASSWORD";
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

}
