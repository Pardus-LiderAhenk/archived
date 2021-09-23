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
package tr.org.liderahenk.lider.authorization;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.authorization.IAuthService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.model.IReportPrivilege;
import tr.org.liderahenk.lider.core.api.ldap.model.ITaskPrivilege;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;

/**
 * Default implementation of {@link IAuthService}. AuthServiceImpl handles
 * authorization of the requests for specified user. Each user LDAP entry has
 * privilege attributes that defines
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class AuthServiceImpl implements IAuthService {

	private final static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private static final String ALL_PERMISSION = "ALL";

	private ILDAPService ldapService;

	private IConfigurationService configurationService;

	@Override
	public List<LdapEntry> getPermittedEntries(final String userDn, final List<LdapEntry> targetEntries,
			final String targetOperation) {

		List<LdapEntry> permittedEntries = new ArrayList<LdapEntry>();

		try {
			logger.debug("Authorization started for DN: {} and operation: {}",
					new Object[] { userDn, targetOperation });

			IUser user = ldapService.getUser(userDn);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDn);
				return null;
			}

			// Find user privileges from LDAP.
			List<? extends ITaskPrivilege> privileges = user.getTaskPrivileges();
			for (ITaskPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				// If everything is permitted, return all entries!
				if (configurationService.getLdapRootDn().equalsIgnoreCase(privilege.getTarget())
						&& ALL_PERMISSION.equalsIgnoreCase(privilege.getOperation())) {
					return targetEntries;
				}

				// If permittedOperations does not contain targetOperation and
				// is not 'ALL', then we can safely skip this privilege.
				if (!ALL_PERMISSION.equalsIgnoreCase(privilege.getOperation())
						&& !privilege.getOperation().equalsIgnoreCase(targetOperation)) {
					continue;
				}

				// Now permitted operation is equals to the target operation,
				// we just need to check each target DNs whether they are one of
				// the permitted DNs
				for (LdapEntry entry : targetEntries) {
					String targetDn = entry.getDistinguishedName();
					if (privilege.getTarget().equalsIgnoreCase(targetDn)
							|| targetDn.indexOf(privilege.getTarget()) >= 0) {
						permittedEntries.add(entry);
					}
				}
			}

		} catch (LdapException e) {
			logger.error(e.getMessage(), e);
		}

		return permittedEntries;
	}

	@Override
	public List<? extends IReportTemplate> getPermittedTemplates(final String userDn,
			final List<? extends IReportTemplate> targetTemplates) {

		List<IReportTemplate> permittedTemplates = new ArrayList<IReportTemplate>();

		try {
			logger.debug("Authorization started for DN: {} and templates: {}",
					new Object[] { userDn, targetTemplates });

			IUser user = ldapService.getUser(userDn);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDn);
				return null;
			}

			List<IReportPrivilege> privileges = user.getReportPrivileges();
			for (IReportPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				// If everything is permitted, return all of the templates
				if (ALL_PERMISSION.equalsIgnoreCase(privilege.getReportCode())) {
					return targetTemplates;
				}

				for (IReportTemplate targetTemplate : targetTemplates) {
					if (privilege.getReportCode().equalsIgnoreCase(targetTemplate.getCode())) {
						permittedTemplates.add(targetTemplate);
					}
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return permittedTemplates;
	}

	@Override
	public List<? extends IReportView> getPermittedViews(String userDn, List<? extends IReportView> targetViews) {

		List<IReportView> permittedViews = new ArrayList<IReportView>();

		try {
			logger.debug("Authorization started for DN: {} and views: {}", new Object[] { userDn, targetViews });

			IUser user = ldapService.getUser(userDn);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDn);
				return null;
			}

			List<IReportPrivilege> privileges = user.getReportPrivileges();
			for (IReportPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				// If everything is permitted, return all of the templates
				if (ALL_PERMISSION.equalsIgnoreCase(privilege.getReportCode())) {
					return targetViews;
				}

				for (IReportView targetView : targetViews) {
					if (privilege.getReportCode().equalsIgnoreCase(targetView.getTemplate().getCode())) {
						permittedViews.add(targetView);
					}
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return permittedViews;
	}

	@Override
	public boolean canGenerateReport(final String userDn, final String reportCode) {
		try {

			logger.debug("Authorization started for DN: {} and report: {}", new Object[] { userDn, reportCode });

			IUser user = ldapService.getUser(userDn);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDn);
				return false;
			}

			List<IReportPrivilege> privileges = user.getReportPrivileges();
			for (IReportPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				// If everything is permitted OR specified report code is one of
				// the permitted ones, return true!
				if (ALL_PERMISSION.equalsIgnoreCase(privilege.getReportCode())
						|| privilege.getReportCode().equalsIgnoreCase(reportCode)) {
					return true;
				}
			}

		} catch (LdapException e) {
			logger.error(e.getMessage(), e);
		}

		return false;
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

}