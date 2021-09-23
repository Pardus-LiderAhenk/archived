package tr.org.liderahenk.ldap.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.SearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class DeleteAgentCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(DeleteAgentCommand.class);

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private ILDAPService ldapService;
	private IAgentDao agentDao;
	private IPluginDbService pluginDbService;
	private IConfigurationService configurationService;

	@Override
	public ICommandResult execute(ICommandContext context) {

		Map<String, Object> params = context.getRequest().getParameterMap();

		String dn = (String) params.get("dn");

		try {
			List<LdapSearchFilterAttribute> filterAttributesList = new ArrayList<LdapSearchFilterAttribute>();

			String removedFileName = configurationService.getAgentLdapRemovedFileName();

			if (removedFileName == null || "".equals(removedFileName)) {
				removedFileName = "Deleted";
			}

			filterAttributesList
					.add(new LdapSearchFilterAttribute("objectClass", "organizationalUnit", SearchFilterEnum.EQ));
			filterAttributesList.add(new LdapSearchFilterAttribute("ou", removedFileName, SearchFilterEnum.EQ));

			List<LdapEntry> deletedOu = ldapService.search(ldapService.getDomainEntry().getDistinguishedName(),
					filterAttributesList, null);

			String ouDn = "ou=" + removedFileName + "," + configurationService.getAgentLdapBaseDn();

			if (deletedOu == null || (deletedOu!=null && deletedOu.isEmpty())) {
				Map<String, String[]> ouMap = new HashMap<String, String[]>();
				ouMap.put("objectClass", new String[] { "top", "organizationalUnit" });
				ouMap.put("ou", new String[] { removedFileName });
				ouMap.put("description", new String[] { "pardusDeviceGroup" });

				ldapService.addEntry(ouDn, ouMap);
				ldapService.moveEntry(dn, ouDn);
			}

			else if (deletedOu != null && deletedOu.size() > 0) {
				ldapService.moveEntry(dn, deletedOu.get(0).getDistinguishedName());
			}

			String newParentDn = ouDn;

			// Find related agent record
			List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "dn", dn, 1);
			IAgent agent = agentList != null && !agentList.isEmpty() ? agentList.get(0) : null;
			if (agent != null) {
				String newDnStr = dn.replace(dn, getCnStrFromDn(dn) + newParentDn);
				agent.setDn(newDnStr);
				agentDao.update(agent);
			}
			logger.info("Entry renamed on db");

			logger.info("Ldap entry deleted successfully");

			/**
			 * deleted agent moved new dn for agent
			 */
			params.put("newParentDn", newParentDn);

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	private String getCnStrFromDn(String dn) {
		String cn = "";
		if (dn != null && !dn.equals("")) {
			String[] dnStr = dn.split(",");
			if (dnStr.length > 1) {
				cn = dnStr[0] + ",";
			}
		}
		return cn;
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "DELETE_AGENT";
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

	public ILDAPService getLdapService() {
		return ldapService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public IAgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
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

	public IConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
