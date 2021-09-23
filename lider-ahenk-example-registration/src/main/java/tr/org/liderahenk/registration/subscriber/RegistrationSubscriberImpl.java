package tr.org.liderahenk.registration.subscriber;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.SearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationResponseMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IScriptResultMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IScriptResultSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IRegistrationDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.utils.FileCopyUtils;
import tr.org.liderahenk.registration.config.RegistrationConfig;

/**
 * 
 * @author M. Edip YILDIZ
 * Nov 29, 2018
 */
public class RegistrationSubscriberImpl implements IRegistrationSubscriber, IScriptResultSubscriber {

	private static Logger logger = LoggerFactory.getLogger(RegistrationSubscriberImpl.class);

	private ILDAPService ldapService;
	private IConfigurationService configurationService;
	private IAgentDao agentDao;
	private IRegistrationDao registrationDao;
	private IEntityFactory entityFactory;
	private IMessageFactory messageFactory;
	private RegistrationConfig registrationConfig;

	private String LDAP_VERSION = "3";

	public String fullJid;

	public ILiderMessage messageReceived(IRegistrationMessage message) throws Exception {

		fullJid = message.getFrom();

		String userLdapRolesDn = configurationService.getUserLdapRolesDn();
		LdapEntry userRoleGroup = null;
		LdapEntry user = null;
		List<LdapEntry> userAuthDomainGroupList = null;

		if (AgentMessageType.REGISTER == message.getType()) {

			logger.info("Example Registration triggered");

			String userName = message.getUserName();
			String userPassword = message.getUserPassword();

			logger.info("Username :" + userName);

			// is user authorized, check user in ldap in
			user = getUserFromLdap(userName, userPassword);

			if (user == null) {
				logger.info("Authorized user not found.. Searched by uid");
				return messageFactory.createRegistrationResponseMessage(null, StatusCode.NOT_AUTHORIZED,
						"User not found", null);
			} else {
				// check if user has admin role
				userAuthDomainGroupList = getUserRoleGroupList(userLdapRolesDn, userName);

				if (userAuthDomainGroupList == null || userAuthDomainGroupList.size() == 0) {
					logger.info("User Found but not authorized. User = " + user.getDistinguishedName());

					return messageFactory.createRegistrationResponseMessage(null, StatusCode.NOT_AUTHORIZED,
							"User not authorized", null);
				}
			}

			boolean alreadyExists = false;

			Entry definedEntry = null;

			logger.info("hostname: " + message.getHostname());
			
			String[] hostnameBirimIdArr = message.getHostname().split("-");
			
			if (hostnameBirimIdArr.length > 0) {
				
				Map<String, Object> propertiesMap = new HashMap<String, Object>();
				propertiesMap.put("unitId", hostnameBirimIdArr[0]);
				
				List<? extends IRegistrationTemplate> req = registrationDao.findByProperties(IRegistrationTemplate.class, propertiesMap, null, 1);
				
				if(req!=null && req.size()>0) {
					IRegistrationTemplate template=req.get(0);
					
					definedEntry= new Entry(message.getHostname(), template.getParentDn().split(","), template.getAuthGroup());
				}
				
				
			}

//			definedEntry = getEnryFromCsvFile(message.getHostname(), registrationConfig.getFileProtocol(),
//					registrationConfig.getFilePath());
			
			if (definedEntry != null) {

				logger.info("Entry found. Defined Entry cn : " + definedEntry.cn
						+ "  role group name "+ definedEntry.roleGroupName
						+ "  ou "+ definedEntry.ouParameters
						);

				for (LdapEntry ldapEntry : userAuthDomainGroupList) {

					if (definedEntry.roleGroupName.trim().contains(ldapEntry.get("cn").trim())) {

						logger.info("finding user role group role group :  " + ldapEntry.getDistinguishedName());

						userRoleGroup = ldapEntry;
						break;
					}
				}

				if (userRoleGroup == null) {

					logger.info("User role group not found in cvs file..");
					return messageFactory.createRegistrationResponseMessage(null, StatusCode.NOT_AUTHORIZED,
							"Entry found but user not found in any role group", null);
				}

				String dn = null;

				// Is Agent registered in ldap?
				final List<LdapEntry> entries = ldapService.search(configurationService.getAgentLdapJidAttribute(),
						message.getFrom().split("@")[0],
						new String[] { configurationService.getAgentLdapJidAttribute() });
				LdapEntry entry = entries != null && !entries.isEmpty() ? entries.get(0) : null;

				// Agent already registered
				if (entry != null) {
					logger.info("This agent already registered in LDAP. Entry : " + entry.getDistinguishedName());
					alreadyExists = true;
					dn = entry.getDistinguishedName();
					logger.info("Updating LDAP entry: {} with password: {}",
							new Object[] { message.getFrom(), message.getPassword() });
					// Update agent LDAP entry.
					 ldapService.updateEntry(dn, "userPassword", message.getPassword());

					//	ldapService.deleteEntry(dn);
					//	dn = createEntryDN(definedEntry.cn, definedEntry.ouParameters);
					//					ldapService.addEntry(dn,
					//							computeAttributes(definedEntry.cn, message.getPassword(), definedEntry.ouParameters));

					logger.info("Agent LDAP entry {} updated successfully!", dn);
				}

				// Agent not registered yet
				else {
					logger.info("Entry is creating..Ldap adding started.");
					// create dn and check is ou level created. if does not
					// exist, create!
					dn = createEntryDN(definedEntry.cn, definedEntry.ouParameters);
					ldapService.addEntry(dn,
							computeAttributes(definedEntry.cn, message.getPassword(), definedEntry.ouParameters));
				}

				// Try to find related agent database record
				List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid",
						message.getFrom().split("@")[0], 1);
				IAgent agent = agents != null && !agents.isEmpty() ? agents.get(0) : null;

				if (agent != null) {
					logger.info("Agent already exists in database.If there is a changed property, it will be updated.");
					alreadyExists = true;
					// Update the record
					agent = entityFactory.createAgent(agent, dn, message.getPassword(), message.getHostname(),
							message.getIpAddresses(), message.getMacAddresses(), message.getData());
					agentDao.update(agent);

				} else {
					// Create new agent database record
					logger.info("Creating new agent record in database.");
					agent = entityFactory.createAgent(null, message.getFrom().split("@")[0], dn, message.getPassword(),
							message.getHostname(), message.getIpAddresses(), message.getMacAddresses(),
							message.getData());
					agentDao.save(agent);
				}

				// set entry to user host for authorization
				logger.info("Setting entry to user host. Entry cn : ", definedEntry.cn);
				ldapService.updateEntryAddAtribute(userRoleGroup.getDistinguishedName(), "sudoHost", definedEntry.cn);

				logger.info("Registration message creating.. ");

				IRegistrationResponseMessage respMessage = messageFactory.createRegistrationResponseMessage(null,
						StatusCode.REGISTERED, dn + " and its related database record created successfully!", dn);

				respMessage.setLdapServer(configurationService.getLdapServer());
				respMessage.setLdapBaseDn(configurationService.getUserLdapBaseDn());
				respMessage.setLdapVersion(LDAP_VERSION);
				respMessage.setLdapUserDn(dn);
				logger.info("Registration message created..  "
						+ "Message details ldap base dn : " +respMessage.getLdapBaseDn() 
						+ "  ldap server =" + respMessage.getLdapBaseDn()
						+ "  ldap userdn =" + respMessage.getLdapUserDn()
						+ "  ldap version =" + respMessage.getLdapVersion()
						);
				return respMessage;

			} else {

				logger.info("Entry not found ...");
				throw new Exception();
			}

		} else if (AgentMessageType.UNREGISTER == message.getType()) {

			String jid = message.getFrom().split("@")[0]; // jid and hostname is same value...

			logger.info("Unregister message from jid : " + jid);
			logger.info("Unregister message UserName: " + message.getUserName());

			user = getUserFromLdap(message.getUserName(), message.getUserPassword());

			if (user == null) {
				logger.info("Authorized user not found");
				return messageFactory.createRegistrationResponseMessage(null, StatusCode.NOT_AUTHORIZED,
						"User not found", null);
			} else {
				// check if user has admin role
				userAuthDomainGroupList = getUserRoleGroupList(userLdapRolesDn, message.getUserName());
				boolean isHostExist = false;
				LdapEntry userRoleLdap = null;
				for (LdapEntry ldapEntry : userAuthDomainGroupList) {

					String hosts = ldapEntry.get("sudoHost");
					
					if (hosts != null && (hosts.contains("ALL") || hosts.contains(jid))) {
						isHostExist = true;
						userRoleLdap = ldapEntry;
					}
					
				}

				if (userAuthDomainGroupList == null || userAuthDomainGroupList.size() == 0 || isHostExist == false) {

					logger.info("User Found but not authorized. User = " + user.getDistinguishedName());
					return messageFactory.createRegistrationResponseMessage(null, StatusCode.NOT_AUTHORIZED,
							"User not authorized", null);
				}

				if (isHostExist) {
					logger.info("Sudo host adding to user role dn. Role Dn= " + userRoleLdap.getDistinguishedName());
					ldapService.updateEntryRemoveAttributeWithValue(userRoleLdap.getDistinguishedName(), "sudoHost",
							jid);

				}
			}
			// Check if agent LDAP entry already exists
			final List<LdapEntry> entry = ldapService.search(configurationService.getAgentLdapJidAttribute(), jid,
					new String[] { configurationService.getAgentLdapJidAttribute() });

			String dn = null;

			// Delete agent LDAP entry
			if (entry != null && !entry.isEmpty()) {
				dn = entry.get(0).getDistinguishedName();
				ldapService.deleteEntry(dn);
			}

			// Find related agent database record.
			List<? extends IAgent> agents = agentDao.findByProperty(IAgent.class, "jid", jid, 1);
			IAgent agent = agents != null && !agents.isEmpty() ? agents.get(0) : null;

			// Mark the record as deleted.
			if (agent != null) {
				agentDao.delete(agent.getId());
			}

			return messageFactory.createRegistrationResponseMessage(null, StatusCode.UNREGISTERED,
					dn + " and its related database record unregistered successfully!", dn);
		}

		throw new Exception();
	}

	private List<LdapEntry> getUserRoleGroupList(String userLdapRolesDn, String userName) throws LdapException {
		List<LdapEntry> userAuthDomainGroupList;
		List<LdapSearchFilterAttribute> filterAttt = new ArrayList();
		
		filterAttt.add(new LdapSearchFilterAttribute("sudoUser", userName, SearchFilterEnum.EQ));
		logger.info("Serching for username " + userName + " in OU " + userLdapRolesDn);
		userAuthDomainGroupList = ldapService.search(userLdapRolesDn, filterAttt,
				new String[] { "cn", "dn", "sudoCommand", "sudoHost", "sudoUser" });
		return userAuthDomainGroupList;
	}

	private LdapEntry getUserFromLdap(String userName, String userPassword) throws LdapException {

		LdapEntry user = null;

		List<LdapSearchFilterAttribute> filterAtt = new ArrayList();
		filterAtt.add(new LdapSearchFilterAttribute("uid", userName, SearchFilterEnum.EQ));
		filterAtt.add(new LdapSearchFilterAttribute("userPassword", userPassword, SearchFilterEnum.EQ));

		List<LdapEntry> userList = ldapService.search(filterAtt, new String[] { "cn", "dn" });
		if (userList != null && userList.size() > 0) {

			user = userList.get(0);
		}
		return user;
	}

	public void setRegistrationConfig(RegistrationConfig registrationConfig) {
		this.registrationConfig = registrationConfig;
	}

	public ILiderMessage postRegistration() throws Exception {
//		String command = "cat /etc/system.properties";
//
//		logger.info("Post-registration triggered");
//		logger.info("Execute script message is sending. Command :{}", command);
//
//		return messageFactory.createExecuteScriptMessage(fullJid, command,
//				configurationService.getFileServerConf(fullJid.split("@")[0]));
		
		return null;
	}

	public void messageReceived(IScriptResultMessage message) throws Exception {

		logger.info("Execute script message result handling.");

		if (message.getResultCode() != null) {
			if (message.getResultCode() > 0) {
				logger.error("Script execution failed. Result code: {}. Eror Message: {}", message.getResultCode(),
						message.getErrorMessage());
			} else if (message.getResultCode() == -1) {
				logger.error(
						"Script couldn not executed properly. Check your command or be sure agent can run this command.");
			}
		} else {
			logger.info("Script executed successfully.");
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("jid", message.getFrom().split("@")[0]);

			List<? extends IAgent> agents = agentDao.findByProperties(IAgent.class, propertiesMap, null, 1);

			if (agents != null && !agents.isEmpty()) {
				IAgent agent = agents.get(0);

				Map<String, Object> newPropertiesMap = new HashMap<String, Object>();

				logger.info("Fetching script result.");

				String filePath = configurationService.getFileServerAgentFilePath().replaceFirst("\\{0\\}",
						message.getFrom().split("@")[0]);
				if (!filePath.endsWith("/"))
					filePath += "/";
				filePath += message.getMd5().toString();

				logger.info("Filepath:{}", filePath);

				byte[] data = new FileCopyUtils().copyFile(configurationService.getFileServerHost(),
						configurationService.getFileServerPort(), configurationService.getFileServerUsername(),
						configurationService.getFileServerPassword(), filePath, "/tmp/lider");

				logger.info("New property adding to agent properties {}", new String(data, StandardCharsets.UTF_8));
				String properties = new String(data, StandardCharsets.UTF_8);
				String[] propertiesArr = properties.split(",");

				logger.info("Parsing properties");

				if (propertiesArr != null && propertiesArr.length > 0) {
					for (String prop : propertiesArr) {
						if (prop.split(":").length > 1) {
							newPropertiesMap.put(prop.split(":")[0].replace("\n", ""),
									prop.split(":")[1].replace("\n", ""));
						}
					}
				}

				entityFactory.createAgent(agent, null, agent.getPassword(), agent.getHostname(), agent.getIpAddresses(),
						agent.getMacAddresses(), newPropertiesMap);
				agentDao.update(agent);
				logger.info("Agent updated with new properties");

			} else {
				logger.error("Jid not found:{}", message.getFrom().split("@")[0]);
			}
		}

	}

	private boolean organizationUnitDoesExist(String dn) throws LdapException {

		logger.info("Checking for  dn->{} entry does exists", dn);

		if (ldapService.getEntry(dn, new String[] {}) != null) {
			logger.debug("Entry: {} already exists", dn);
			return true;
		} else {
			logger.debug("{} not found", dn);
			return false;
		}
	}

	private Map<String, String[]> computeAttributes(final String cn, final String password, String[] dcParameters) {

		Map<String, String[]> attributes = new HashMap<String, String[]>();
		// attributes.put("objectClass", new String[] { "device", "pardusDevice",
		// "orgNo" });
		attributes.put("objectClass", new String[] { "device", "pardusDevice" });
		attributes.put("cn", new String[] { cn });
		attributes.put("uid", new String[] { fullJid.split("@")[0] });
		attributes.put("userPassword", new String[] { password });
		// TODO fixing about LDAP
		String ahenkBaseDn = configurationService.getAgentLdapBaseDn();
		attributes.put("owner", new String[] { ahenkBaseDn != null ? ahenkBaseDn : "Ahenkler" });
		// attributes.put("sunucuNo", new String[] { "9999"});
		return attributes;
	}

	private String createEntryDN(String cn, String[] ouParameters) throws LdapException {
		String incrementaldn = configurationService.getAgentLdapBaseDn();

		for (String ouValue : ouParameters) {
			incrementaldn = "ou=" + ouValue + "," + incrementaldn;

			if (organizationUnitDoesExist(incrementaldn) == false) {
				logger.info(" {} entry adding to ldap hierarchy", ouValue);
				Map<String, String[]> ouMap = new HashMap<String, String[]>();
				ouMap.put("objectClass", new String[] { "top", "organizationalUnit", "pardusLider" });
				ouMap.put("ou", new String[] { ouValue });
				ouMap.put("description", new String[] { "pardusDeviceGroup" });
				ldapService.addEntry(incrementaldn, ouMap);
			}
		}

		incrementaldn = "cn=" + cn + "," + incrementaldn;
		return incrementaldn;
	}

	public class Entry {
		String cn;
		String[] ouParameters;
		String roleGroupName;

		public Entry(String cn, String[] ouParameters, String roleGroupName) {
			this.cn = cn;
			this.ouParameters = ouParameters;
			this.roleGroupName = roleGroupName;
		}
	}

	// public Entry getEnryFromCsvFile(String strMacAddresses, String protocol,
	// String path) throws Exception {
	// logger.info(
	// "Reading csv file according to parameters of configuration protocol:" +
	// protocol + ", path:" + path);
	public Entry getEnryFromCsvFile(String strhostname, String protocol, String path) throws Exception {
		logger.info(
				"Reading csv file according to parameters of configuration protocol:" + protocol + ", path:" + path);
		CsvReader csvReader = new CsvReader();
		Map<String, String[]> expectedRecordsMap = csvReader.read(protocol, path);

		String cn = strhostname;

		String[] hostnameBirimIdArr = strhostname.split("-");

		String[] ouParametersLineArr = null;
		String[] ouParameters = null;

		String roleGroupName = null;

		// 60016,TT_Ankara,BilgiIslem birim id deki satir bulunur. 2. parametre role
		// grup adi, ilk parametre birim id deki kontrol degeri.

		if (hostnameBirimIdArr.length > 0) {

			String hostnameBirimId = hostnameBirimIdArr[0];

			String hostnameBirimIdCheck = "";

			if (hostnameBirimId.length() >= 5) {
				hostnameBirimIdCheck = hostnameBirimId.substring(hostnameBirimId.length() - 5);
			}
			// records dosyasndaki ilgili kayit bulunur.
			ouParametersLineArr = expectedRecordsMap.get(hostnameBirimIdCheck.replace("'", ""));

			// role grup adi 1. index.
			roleGroupName = ouParametersLineArr[0];

			ouParameters = Arrays.copyOfRange(ouParametersLineArr, 1, ouParametersLineArr.length);

			// for (String hostname1 : hostname) {
			// hostname1 = hostname1.trim();
			// ouParameters = expectedRecordsMap.get(hostname1.replace("'", ""));
			// if (ouParameters != null && ouParameters.length > 1) {
			// cn = ouParameters[0];
			// break;
			// }
			// }

		}
		if (ouParameters == null || cn == null || roleGroupName == null) {
			return null;
		}
		return new Entry(cn, ouParameters, roleGroupName);
	}

	/**
	 * 
	 * @param ldapService
	 */
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * 
	 * @param agentDao
	 */
	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param messageFactory
	 */
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public IRegistrationDao getRegistrationDao() {
		return registrationDao;
	}

	public void setRegistrationDao(IRegistrationDao registrationDao) {
		this.registrationDao = registrationDao;
	}

}
