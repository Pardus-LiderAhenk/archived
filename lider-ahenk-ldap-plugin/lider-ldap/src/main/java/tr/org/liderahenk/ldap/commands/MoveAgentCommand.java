package tr.org.liderahenk.ldap.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
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

public class MoveAgentCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(MoveAgentCommand.class);
	
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private ILDAPService ldapService;
	private IAgentDao agentDao;
	private IPluginDbService pluginDbService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		
		Map<String, Object> params= context.getRequest().getParameterMap();
		
		String dn= (String) params.get("dn");
		String newParentDn= (String) params.get("newParentDn");
		
		
		try {
			
			// Find related agent record
			List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "dn", dn, 1);
			IAgent agent = agentList != null && !agentList.isEmpty() ? agentList.get(0) : null;
			if(agent != null) {
				String newDnStr= dn.replace(dn, getCnStrFromDn(dn)+newParentDn);
				agent.setDn(newDnStr);
				agentDao.update(agent);
			}
			logger.info("Entry renamed on db");
			
			ldapService.moveEntry(dn, newParentDn);
			logger.info("Ldap entry moved successfully");
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}
	
	
	private String getCnStrFromDn(String dn) {
			String cn="";
			if(dn!=null && !dn.equals("")) {
				String[] dnStr= dn.split(",");
				if(dnStr.length>1) {
					cn=dnStr[0]+",";
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
		return "MOVE_AGENT";
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
	
}
