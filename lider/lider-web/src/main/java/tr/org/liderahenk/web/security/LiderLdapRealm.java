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
package tr.org.liderahenk.web.security;

import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.ldap.AbstractLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;

/**
 * Main realm class which works as a security-specific DAO for Shiro to
 * understand Subject programming API.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see http://shiro.apache.org/terminology.html
 *
 */
public class LiderLdapRealm extends AbstractLdapRealm {

	private static Logger logger = LoggerFactory.getLogger(LiderLdapRealm.class);

	private boolean useSSL;
	private String objectClasses;
	private String userIdAttribute;

	@Autowired
	private IConfigurationService config;

	public void initRealm() {

		logger.debug("Initializing LDAP realm.");

		this.searchBase = config.getUserLdapBaseDn();
		this.useSSL = config.getLdapUseSsl();

		if (useSSL) {
			this.url = "ldaps://" + config.getLdapServer() + ":" + config.getLdapPort();
		} else {
			this.url = "ldap://" + config.getLdapServer() + ":" + config.getLdapPort();
		}

		this.systemUsername = config.getLdapUsername();
		this.systemPassword = config.getLdapPassword();

		this.objectClasses = config.getUserLdapObjectClasses();
		this.userIdAttribute = config.getUserLdapUidAttribute();

		logger.debug("searchBase => {}, url => {}, systemUsername => {}, systemPassword => {}",
				new Object[] { searchBase, url, systemUsername, systemPassword });
		logger.debug("user object classes => {}", objectClasses);
		logger.debug("user id attribute => {}", userIdAttribute);
		logger.info("Successfully initialized LDAP realm.");
	}

	public void reload(Map<String, ?> properties) {
		logger.debug("Config admin update received: {} ", properties);
		this.searchBase = (String) properties.get("searchBase");
	}

	@Override
	protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token,
			LdapContextFactory contextFactory) throws NamingException {

		logger.debug("queryForAuthenticationInfo, principal: {}, credentials: *****", token.getPrincipal());
		logger.debug("contextFactory : {}", contextFactory);

		try {
			if (token == null || token.getPrincipal() == null) {
				logger.info("No authentication token provided, will not try to authenticate..");
				return null;
			}

			LdapContext sysCtx = contextFactory.getSystemLdapContext();

			String objClsFilter = createObjectClassFilter(objectClasses);
			String userIdFilter = createAttributeFilter(userIdAttribute, token.getPrincipal().toString());

			String filter = mergeFiltersAND(objClsFilter, userIdFilter);

			NamingEnumeration<?> namingEnumeration = sysCtx.search(config.getUserLdapBaseDn(), filter,
					getSimpleSearchControls());

			while (namingEnumeration.hasMore()) {

				SearchResult result = (SearchResult) namingEnumeration.next();

				String dn = result.getNameInNamespace();

				try {
					contextFactory.getLdapContext(dn, token.getCredentials());

					return new SimpleAuthenticationInfo(dn, token.getCredentials(), "StaticRealm");

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principalCollection,
			LdapContextFactory contextFactory) throws NamingException {
		logger.debug("queryForAuthorizationInfo, principalCollection.getPrimaryPrincipal: {}",
				principalCollection.getPrimaryPrincipal());
		logger.debug("contextFactory : {}", contextFactory);
		return null;
	}

	private String mergeFiltersOR(String filter1, String filter2) {
		String ORTemplate = "(|({0})({1}))";
		return ORTemplate.replace("{0}", filter1).replace("{1}", filter2);
	}

	private String mergeFiltersAND(String filter1, String filter2) {
		String ORTemplate = "(&({0})({1}))";
		return ORTemplate.replace("{0}", filter1).replace("{1}", filter2);
	}

	private String createObjectClassFilter(String objectClassesCommaSeparatedValues) {

		String[] objectClasses = objectClassesCommaSeparatedValues.split(",");
		String[] filters = new String[objectClasses.length];

		String filterTemplate = "objectClass={0}";

		for (int i = 0; i < objectClasses.length; i++) {
			String objectClassFilter = filterTemplate.replace("{0}", objectClasses[i]);
			filters[i] = objectClassFilter;
		}

		String resultingFilter = null;
		for (int i = 0; i < filters.length; i++) {
			if (null != resultingFilter) {
				resultingFilter = mergeFiltersOR(resultingFilter, filters[i]);
			} else {
				resultingFilter = filters[i];
			}
		}

		return resultingFilter;
	}

	private String createAttributeFilter(String attributeName, String attributeValue) {
		String filterTemplate = "{0}={1}";

		return filterTemplate.replace("{0}", attributeName).replace("{1}", attributeValue);
	}

	private SearchControls getSimpleSearchControls() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(5000);
		return searchControls;
	}

}
