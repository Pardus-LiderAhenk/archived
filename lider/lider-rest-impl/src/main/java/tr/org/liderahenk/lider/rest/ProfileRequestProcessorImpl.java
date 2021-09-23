/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.mail.IMailService;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IMailAddressDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IProfileRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.utils.LiderCoreUtils;
import tr.org.liderahenk.lider.core.api.utils.StringJoinCursor;

/**
 * Processor class for handling/processing profile data.
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoğlu</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ProfileRequestProcessorImpl implements IProfileRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(ProfileRequestProcessorImpl.class);

	private IProfileDao profileDao;
	private IPluginDao pluginDao;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IPolicyDao policyDao;
	private IEntityFactory entityFactory;
	private IConfigurationService configService;
	private IMailService mailService;
	private IMailAddressDao mailAddressDao;
	private ILDAPService ldapService;
	private ICommandDao commandDao;

	private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy H:m");

	@Override
	public IRestResponse add(String json) {
		try {
			IProfileRequest request = requestFactory.createProfileRequest(json);

			IPlugin plugin = findRelatedPlugin(request.getPluginName(), request.getPluginVersion());
			IProfile profile = entityFactory.createProfile(plugin, request);
			profile = profileDao.save(profile);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("profile", profile);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse update(String json) {
		try {
			IProfileRequest request = requestFactory.createProfileRequest(json);

			IProfile profile = profileDao.find(request.getId());
			profile = entityFactory.createProfile(profile, request);
			profile = profileDao.update(profile);

			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("profiles.id", profile.getId());
			logger.debug("Finding policies by given properties.");
			List<? extends IPolicy> policies = policyDao.findByProperties(null, propertiesMap, null, null);
			if (policies != null) {
				logger.debug("policies.size(): " + policies.size());
				for (IPolicy policy : policies) {
					logger.debug("Updating policy: " + policy.getId());
					incrementPolicyVersion(policy);
					// Is policy applied to some LDAP entry
					ICommand command = commandDao.getCommandByPolicyId(policy.getId());
					if (command != null) {
						sendMail(policy, profile, command);
					}
				}
			}

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("profile", profile);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	private void sendMail(IPolicy policy, IProfile updatedProfile, ICommand command) {
		String mailSubject = "Lider Ahenk Politikası";
		StringBuilder mailContent = new StringBuilder();
		final List<String> toList = new ArrayList<String>();

		mailContent
				.append("Aşağıda isimleri verilen eklentilerden oluşan ve detaylarıyla aşağıda belirtilen LDAP ögelerine ")
				.append(format.format(policy.getCreateDate())).append(" tarihinde uygulanmış \"")
				.append(policy.getLabel()).append("\" isimli politikadaki \"").append(updatedProfile.getLabel())
				.append("\" isimli profil kaydında değişiklik yapılmıştır:\n\n");
		mailContent.append("Politikayı oluşturan eklentiler:\n");

		List<LdapEntry> targetEntries = ldapService.findTargetEntries(command.getDnList(), command.getDnType());

		Set<? extends IProfile> profiles = policy.getProfiles();
		List<String> plugins = new ArrayList<String>();
		StringBuilder profileContent = new StringBuilder();
		for (IProfile profile : profiles) {
			Map<String, Object> profileData = profile.getProfileData();
			// Plugin description
			plugins.add(profile.getPlugin().getDescription());
			if (profileData != null) {
				Boolean mailSend = (Boolean) profileData.get("mailSend");
				if (mailSend != null && mailSend.booleanValue()) {
					// Add profile content
					profileContent
							.append(replaceValues(profileData.get("mailContent").toString(), profileData, command));

					// Add admin recipients
					List<? extends IMailAddress> mailAddressList = mailAddressDao.findByProperty(IMailAddress.class,
							"plugin.id", profile.getPlugin().getId(), 0);
					if (mailAddressList != null) {
						for (IMailAddress iMailAddress : mailAddressList) {
							toList.add(iMailAddress.getMailAddress());
						}
					}
				}
			}
		}
		mailContent.append(StringUtils.join(plugins, ","));
		// LDAP entries and their details (TCK, username etc)
		mailContent.append("\n\nPolitikanın uygulandığı LDAP ögeleri:\n");
		mailContent.append(LiderCoreUtils.join(targetEntries, ",\n", new StringJoinCursor() {
			@Override
			public String getValue(Object object) {
				if (object instanceof LdapEntry) {
					LdapEntry entry = (LdapEntry) object;
					Map<String, String> attributes = entry.getAttributes();
					StringBuilder attrStr = new StringBuilder();
					if (attributes != null) {
						for (Entry<String, String> attr : attributes.entrySet()) {
							// Ignore liderPrivilege attribute...
							if (attr.getKey().equalsIgnoreCase(configService.getUserLdapPrivilegeAttribute())) {
								continue;
							}
							attrStr.append(attr.getKey()).append("=").append(attr.getValue());
						}
						String email = attributes.get(configService.getLdapEmailAttribute());
						// Add personnel email to recipients
						if (email != null && !email.isEmpty()) {
							toList.add(email);
						}
					}
					return "DN: " + entry.getDistinguishedName() + " Öznitelikler: [" + attrStr.toString() + "]";
				}
				return LiderCoreUtils.EMPTY;
			}
		}));
		mailContent.append("\n\nPolitika parametreleri:\n");
		mailContent.append(profileContent).append("\n\n");
		if (toList.size() > 0) {
			mailService.sendMail(toList, mailSubject, mailContent.toString());
		}
	}

	private Pattern EXPRESSION = Pattern.compile("\\{(.*?)\\}");

	private String replaceValues(String message, Map<String, Object> values, ICommand command) {
		Matcher m = EXPRESSION.matcher(message);
		while (m.find()) {
			String expr = m.group(1);
			Object value = null;
			if (values.containsKey(expr)) {
				message = message.replaceAll("\\{" + expr + "\\}", values.get(expr).toString());
			} else if ((value = LiderCoreUtils.getFieldValueIfExists(command, expr)) != null) {
				message = message.replaceAll("\\{" + expr + "\\}", value.toString());
			} else {
				message = message.replaceAll("\\{" + expr + "\\}", "");
			}
		}
		return message;
	}

	@Override
	public IRestResponse list(String pluginName, String pluginVersion, String label, Boolean active) {

		IPlugin plugin = findRelatedPlugin(pluginName, pluginVersion);

		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("plugin.id", plugin.getId());
		propertiesMap.put("deleted", false);
		if (label != null && !label.isEmpty()) {
			propertiesMap.put("label", label);
		}
		if (active != null) {
			propertiesMap.put("active", active);
		}

		// Find desired profiles
		List<? extends IProfile> profiles = profileDao.findByProperties(IProfile.class, propertiesMap, null, null);
		logger.debug("Found profiles: {}", profiles);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("profiles", profiles);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IProfile profile = profileDao.find(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("profile", profile);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse delete(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		profileDao.delete(new Long(id));
		logger.info("Profile record deleted: {}", id);

		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("profiles.id", id);

		logger.debug("Finding policies by given properties.");
		List<? extends IPolicy> policies = policyDao.findByProperties(null, propertiesMap, null, null);
		if (policies != null) {
			logger.debug("policies.size(): " + policies.size());
			for (IPolicy policy : policies) {
				logger.debug("Updating policy: " + policy.getId());
				incrementPolicyVersion(policy);
			}
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	/**
	 * Increments version number of a policy by one.
	 * 
	 * @param policy
	 */
	private void incrementPolicyVersion(IPolicy policy) {
		if (policy.getPolicyVersion() != null) {
			String oldVersion = policy.getPolicyVersion().split("-")[1];
			Integer newVersion = new Integer(oldVersion) + 1;
			policy.setPolicyVersion(policy.getId() + "-" + newVersion);
			policyDao.update(policy);
			logger.debug(
					"Version of policy: " + policy.getId() + " is increased from " + oldVersion + " to " + newVersion);
		}
	}

	/**
	 * Find IPlugin instance by given plugin name and version.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @return
	 */
	private IPlugin findRelatedPlugin(String pluginName, String pluginVersion) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("name", pluginName);
		propertiesMap.put("version", pluginVersion);
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		IPlugin plugin = plugins.get(0);
		return plugin;
	}

	public void setProfileDao(IProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public void setPolicyDao(IPolicyDao policyDao) {
		this.policyDao = policyDao;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

	public void setMailAddressDao(IMailAddressDao mailAddressDao) {
		this.mailAddressDao = mailAddressDao;
	}

	public void setConfigService(IConfigurationService configService) {
		this.configService = configService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}
