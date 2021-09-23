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
package tr.org.liderahenk.lider.ldap;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.LdapResult;
import org.apache.directory.api.ldap.model.message.ModifyRequest;
import org.apache.directory.api.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.PoolableLdapConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.caching.ICacheService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.SearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.model.IReportPrivilege;
import tr.org.liderahenk.lider.core.api.ldap.model.ITaskPrivilege;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.rest.enums.DNType;
import tr.org.liderahenk.lider.ldap.model.ReportPrivilegeImpl;
import tr.org.liderahenk.lider.ldap.model.TaskPrivilegeImpl;
import tr.org.liderahenk.lider.ldap.model.UserImpl;

/**
 * Default implementation for {@link ILDAPService}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class LDAPServiceImpl implements ILDAPService {

	private final static Logger logger = LoggerFactory.getLogger(LDAPServiceImpl.class);

	private IConfigurationService configurationService;
	private ICacheService cacheService;

	private LdapConnectionPool pool;

	/**
	 * Pattern for task privileges (e.g. [TASK:dc=mys,dc=pardus,dc=org:ALL],
	 * [TASK:dc=mys,dc=pardus,dc=org:EXECUTE_SCRIPT] )
	 */
	private static Pattern taskPriviligePattern = Pattern.compile("\\[TASK:(.+):(.+)\\]");

	/**
	 * Pattern for report privileges (e.g. [REPORT:ONLINE-USERS-REPORT] ,
	 * [REPORT:ALL] )
	 */
	private static Pattern reportPriviligePattern = Pattern.compile("\\[REPORT:([a-zA-Z0-9-,]+)\\]");

	public void init() throws Exception {

		LdapConnectionConfig lconfig = new LdapConnectionConfig();
		lconfig.setLdapHost(configurationService.getLdapServer());
		lconfig.setLdapPort(Integer.parseInt(configurationService.getLdapPort()));
		lconfig.setName(configurationService.getLdapUsername());
		lconfig.setCredentials(configurationService.getLdapPassword());
		
		
		if (configurationService.getLdapUseSsl()) {
			lconfig.setUseSsl(true);
			if (configurationService.getLdapAllowSelfSignedCert()) {
				lconfig.setKeyManagers(createCustomKeyManagers());
				lconfig.setTrustManagers(createCustomTrustManager());
			}
		} else {
			lconfig.setUseSsl(false);
		}
		
		// Create connection pool
		PoolableLdapConnectionFactory factory = new PoolableLdapConnectionFactory(lconfig);
		pool = new LdapConnectionPool(factory);
		pool.setTestOnBorrow(true);
		pool.setMaxActive(100);
		pool.setMaxWait(3000);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);


		logger.debug(this.toString());
	}

	private TrustManager createCustomTrustManager() {
		return new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}
		};
	}

	private KeyManager[] createCustomKeyManagers() {
		KeyManager[] bypassKeyManagers = new KeyManager[] { new X509KeyManager() {

			@Override
			public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
				return null;
			}

			@Override
			public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
				return null;
			}

			@Override
			public X509Certificate[] getCertificateChain(String arg0) {
				return null;
			}

			@Override
			public String[] getClientAliases(String arg0, Principal[] arg1) {
				return null;
			}

			@Override
			public PrivateKey getPrivateKey(String arg0) {
				return null;
			}

			@Override
			public String[] getServerAliases(String arg0, Principal[] arg1) {
				return null;
			}

		} };
		return bypassKeyManagers;
	}

	public void destroy() {
		logger.info("Destroying LDAP service...");
		try {
			pool.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @return new LDAP connection
	 * @throws LdapException
	 */
	@Override
	public LdapConnection getConnection() throws LdapException {
		LdapConnection connection = null;
		try {
			connection = pool.getConnection();
		} catch (Exception e) {
			throw new LdapException(e);
		}
		return connection;
	}

	/**
	 * Try to release specified connection
	 * 
	 * @param ldapConnection
	 */
	@Override
	public void releaseConnection(LdapConnection ldapConnection) {
		try {
			pool.releaseConnection(ldapConnection);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Find user LDAP entry from given DN parameter. Use this method only if you
	 * want to <b>read his/her (task and report) privileges</b>, otherwise use
	 * getEntry() or search() methods since they are more efficient.
	 * 
	 * @param userDn
	 * @return
	 * @throws LdapException
	 */
	@Override
	public IUser getUser(String userDn) throws LdapException {

		LdapConnection connection = null;
		UserImpl user = null;

		user = (UserImpl) cacheService.get("ldap:getuser:" + userDn);

		if (user != null) {
			logger.debug("Cache hit. User DN: {}", userDn);
			return user;
		}

		logger.debug("Cache miss: user DN: {}, doing ldap search", userDn);
		try {
			connection = getConnection();
			Entry resultEntry = connection.lookup(userDn);
			if (null != resultEntry) {
				user = new UserImpl();

				if (null != resultEntry.get(configurationService.getUserLdapUidAttribute())) {
					// Set user's UID/JID
					user.setUid(resultEntry.get(configurationService.getUserLdapUidAttribute()).getString());
				}

				if (null != resultEntry.get(configurationService.getUserLdapPrivilegeAttribute())) {
					// Set task & report privileges
					user.setTaskPrivileges(new ArrayList<ITaskPrivilege>());
					user.setReportPrivileges(new ArrayList<IReportPrivilege>());
					Iterator<Value<?>> iter = resultEntry.get(configurationService.getUserLdapPrivilegeAttribute())
							.iterator();
					while (iter.hasNext()) {
						String privilege = iter.next().getValue().toString();
						addUserPrivilege(user, privilege);
					}

					// Find group privileges if this user belongs to a group
					LdapConnection connection2 = null;
					EntryCursor cursor = null;

					try {
						connection2 = getConnection();

						String filter = "(&(objectClass=pardusLider)(member=$1))".replace("$1", userDn);
						cursor = connection2.search(configurationService.getLdapRootDn(), filter, SearchScope.SUBTREE);
						while (cursor.next()) {
							Entry entry = cursor.get();
							if (null != entry) {
								logger.debug("Found user group: {}", entry.getDn());
								if (null != entry.get("liderPrivilege")) {
									Iterator<Value<?>> iter2 = entry.get("liderPrivilege").iterator();
									while (iter2.hasNext()) {
										String privilege = iter2.next().getValue().toString();
										addUserPrivilege(user, privilege);
									}
								} else {
									logger.debug("No privilege found in group => {}", entry.getDn());
								}
							}
						}
						logger.debug("Finished processing group privileges for user {}", userDn);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						throw new LdapException(e);
					} finally {
						if (cursor != null) {
							cursor.close();
						}
						releaseConnection(connection2);
					}
				}

				logger.debug("Putting user to cache: user DN: {}", userDn);
				cacheService.put("ldap:getuser:" + userDn, user);

				return user;
			}

			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	private void addUserPrivilege(UserImpl user, String privilege) {
		String[] privBlocks = privilege != null ? privilege.split("\\|") : null;
		logger.debug("Found privilege: {}", privilege);
		if (privBlocks != null) {
			for (String privBlock : privBlocks) {
				Matcher tMatcher = taskPriviligePattern.matcher(privBlock);
				Matcher rMatcher = reportPriviligePattern.matcher(privBlock);
				if (tMatcher.matches()) { // Task privilege
					String targetEntry = tMatcher.group(1);
					String[] taskCodes = tMatcher.group(2).split(",");
					for (String taskCode : taskCodes) {
						user.getTaskPrivileges().add(new TaskPrivilegeImpl(targetEntry, taskCode));
					}
				} else if (rMatcher.matches()) { // Report privilege
					String[] reportCodes = rMatcher.group(1).split(",");
					for (String reportCode : reportCodes) {
						user.getReportPrivileges().add(new ReportPrivilegeImpl(reportCode));
					}
				} else {
					logger.warn("Invalid pattern in privilege => {}", privBlock);
				}
			}
		}
	}

	/**
	 * Create new LDAP entry
	 */
	@Override
	public void addEntry(String newDn, Map<String, String[]> attributes) throws LdapException {

		LdapConnection connection = null;

		try {
			connection = getConnection();

			Dn dn = new Dn(newDn);
			Entry entry = new DefaultEntry(dn);

			for (Map.Entry<String, String[]> Entry : attributes.entrySet()) {
				String[] entryValues = Entry.getValue();
				for (String value : entryValues) {
					entry.add(Entry.getKey(), value);
				}
			}

			AddRequest addRequest = new AddRequestImpl();
			addRequest.setEntry(entry);

			AddResponse addResponse = connection.add(addRequest);
			LdapResult ldapResult = addResponse.getLdapResult();

			if (ResultCodeEnum.SUCCESS.equals(ldapResult.getResultCode())) {
				return;
			} else {
				logger.error("Could not create LDAP entry: {}", ldapResult.getDiagnosticMessage());
				throw new LdapException(ldapResult.getDiagnosticMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	/**
	 * Delete specified LDAP entry
	 * 
	 * @param dn
	 * @throws LdapException
	 */
	@Override
	public void deleteEntry(String dn) throws LdapException {
		LdapConnection connection = getConnection();
		try {
			connection.delete(new Dn(dn));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public void updateEntry(String entryDn, String attribute, String value) throws LdapException {
		logger.info("Replacing attribute " + attribute + " value " + value);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {
				if (entry.get(attribute) != null) {
					Value<?> oldValue = entry.get(attribute).get();
					entry.remove(attribute, oldValue);
				}
				entry.add(attribute, value);
				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}
	
	
	@Override
	public void renameEntry(String oldName, String newName) throws LdapException {
		logger.info("Rename DN  Old Name :" + oldName + " New Name " + newName);
		LdapConnection connection = null;
		
		connection = getConnection();
		
		Entry entry = null;
		try {
			entry = connection.lookup(oldName);
			
			org.apache.directory.api.ldap.model.name.Rdn rdn= new org.apache.directory.api.ldap.model.name.Rdn(newName);
		
			connection.rename(entry.getDn(), rdn, true);
				
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}
	@Override
	public void moveEntry(String entryDn, String newSuperiorDn) throws LdapException {
		logger.info("Moving entryDn :" + entryDn + "  newSuperiorDn " + newSuperiorDn);
		LdapConnection connection = null;
		
		connection = getConnection();
		
		try {
			
			connection.move(entryDn,newSuperiorDn);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public void updateEntryAddAtribute(String entryDn, String attribute, String value) throws LdapException {
		logger.info("Adding attribute " + attribute + " value " + value);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {
				entry.put(attribute, value);

				ModifyRequest mr = new ModifyRequestImpl();
				mr.setName(new Dn(entryDn));
				mr.add(attribute, value);

				connection.modify(mr);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}


	@Override
	public void updateEntryRemoveAttribute(String entryDn, String attribute) throws LdapException {

		logger.info("Removing attribute: {}", attribute);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {
				boolean isAttributeExist=false;
				
				for (Attribute a : entry.getAttributes()) {
					if (a.getId().contains(attribute) || ( a.getAttributeType()!=null && a.getAttributeType().getName().equalsIgnoreCase(attribute))) {
						isAttributeExist=true;
						entry.remove(a);
					}
				}

				if(isAttributeExist)
				connection.modify(entry, ModificationOperation.REMOVE_ATTRIBUTE);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
	}

	@Override
	public void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value)
			throws LdapException {

		logger.info("Removing attribute: {}", attribute);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {

				for (Attribute a : entry.getAttributes()) {
					if (a.contains(value)) {
						a.remove(value);
					}
				}

				
//				if (entry.get(attribute) != null) {
//					Value<?> oldValue = entry.get(attribute).get();
//					entry.remove(attribute, oldValue);
//				}
//				entry.add(attribute, value);
				
				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

	}

	/**
	 * @return LDAP root DN
	 */
	@Override
	public Entry getRootDSE() throws LdapException {
		LdapConnection connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.getRootDse();
		} catch (org.apache.directory.api.ldap.model.exception.LdapException e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}
		return entry;
	}

	@Override
	public LdapEntry getEntry(String entryDn, String[] returningAttributes) throws LdapException {

		LdapConnection conn = null;
		EntryCursor cursor = null;

		try {
			conn = getConnection();

			// Add 'objectClass' to requested attributes to determine entry type
			Set<String> requestAttributeSet = new HashSet<String>();
			requestAttributeSet.add("objectClass");
			if (returningAttributes != null) {
				requestAttributeSet.addAll(Arrays.asList(returningAttributes));
			}

			// Search for entries
			cursor = conn.search(entryDn, "(objectClass=*)", SearchScope.OBJECT,
					requestAttributeSet.toArray(new String[requestAttributeSet.size()]));
			if (cursor.next()) {
				Entry entry = cursor.get();
				Map<String, String> attributes = new HashMap<String, String>();
				for (String attr : returningAttributes) {
					try {
						attributes.put(attr, entry.get(attr).getString());
					} catch (Exception e) {
						logger.error("Cannot find attribute: {} in entry: {}", new Object[] { attr, entry.getDn() });
					}
				}
				return new LdapEntry(entryDn, attributes, convertObjectClass2DNType(entry.get("objectClass")));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			releaseConnection(conn);
		}
	}

	@Override
	public String getDN(String baseDn, String attributeName, String attributeValue) throws LdapException {

		LdapConnection connection = null;
		EntryCursor cursor = null;

		String filter = "(" + attributeName + "=" + attributeValue + ")";

		try {
			connection = getConnection();
			cursor = connection.search(baseDn, filter, SearchScope.SUBTREE);
			while (cursor.next()) {
				return cursor.get().getDn().getName();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LdapException(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			releaseConnection(connection);
		}

		return null;
	}

	/**
	 * Main search method for LDAP entries.
	 * 
	 * @param baseDn
	 *            search base DN
	 * @param filterAttributes
	 *            filtering attributes used to construct query condition
	 * @param returningAttributes
	 *            returning attributes
	 * @return list of LDAP entries
	 * @throws LdapException
	 */
	@Override
	public List<LdapEntry> search(String baseDn, List<LdapSearchFilterAttribute> filterAttributes,
			String[] returningAttributes) throws LdapException {

		List<LdapEntry> result = new ArrayList<LdapEntry>();
		LdapConnection connection = null;

		Map<String, String> attrs = null;

		try {
			connection = getConnection();

			SearchRequest req = new SearchRequestImpl();
			req.setScope(SearchScope.SUBTREE);

			// Add 'objectClass' to requested attributes to determine entry type
			Set<String> requestAttributeSet = new HashSet<String>();
			requestAttributeSet.add("objectClass");
			if (returningAttributes != null) {
				requestAttributeSet.addAll(Arrays.asList(returningAttributes));
			}
			req.addAttributes(requestAttributeSet.toArray(new String[requestAttributeSet.size()]));

			// Construct filter expression
			String searchFilterStr = "(&";
			for (LdapSearchFilterAttribute filterAttr : filterAttributes) {
				searchFilterStr = searchFilterStr + "(" + filterAttr.getAttributeName()
						+ filterAttr.getOperator().getOperator() + filterAttr.getAttributeValue() + ")";
			}
			searchFilterStr = searchFilterStr + ")";
			req.setFilter(searchFilterStr);

			req.setTimeLimit(0);
			baseDn = baseDn.replace("+", " ");
			req.setBase(new Dn(baseDn));

			SearchCursor searchCursor = connection.search(req);
			while (searchCursor.next()) {
				Response response = searchCursor.get();
				attrs = new HashMap<String, String>();
				if (response instanceof SearchResultEntry) {
					Entry entry = ((SearchResultEntry) response).getEntry();
					if (returningAttributes != null) {
						for (String attr : returningAttributes) {
							
							for(Attribute att : entry.getAttributes()) {
								if(attr.equalsIgnoreCase(att.getId()))	{
									String attrValue="";
									for (Value<?> value : att) {
											attrValue  += value.getString() +" ";
											
									} 
									attrs.put(attr, attrValue != null ? attrValue : "");
								}
							}
							
						}
					}
					result.add(new LdapEntry(entry.getDn().toString(), attrs,
							convertObjectClass2DNType(entry.get("objectClass"))));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

		return result;
	}

	/**
	 * Convenience method for main search method
	 */
	@Override
	public List<LdapEntry> search(List<LdapSearchFilterAttribute> filterAttributes, String[] returningAttributes)
			throws LdapException {
		return search(configurationService.getLdapRootDn(), filterAttributes, returningAttributes);
	}

	/**
	 * Yet another convenience method for main search method
	 */
	@Override
	public List<LdapEntry> search(String attributeName, String attributeValue, String[] returningAttributes)
			throws LdapException {
		List<LdapSearchFilterAttribute> filterAttributes = new ArrayList<LdapSearchFilterAttribute>();
		filterAttributes.add(new LdapSearchFilterAttribute(attributeName, attributeValue, SearchFilterEnum.EQ));
		return search(configurationService.getLdapRootDn(), filterAttributes, returningAttributes);
	}
	
	
	@Override
	public List<LdapEntry> findSubEntries(String dn, String filter, String[] returningAttributes,SearchScope scope) throws LdapException {
		List<LdapEntry> result = new ArrayList<LdapEntry>();
		LdapConnection connection = null;
		
		Map<String, String> attrs = null;
		
		connection = getConnection();
		try {
			connection = getConnection();
			
			SearchRequest request= new SearchRequestImpl();
			
			dn = dn.replace("+", " ");
			request.setBase(new Dn(dn));
			request.setScope(scope);
			request.setFilter(filter);  //"(objectclass=*)"
			
			for (String attr : returningAttributes) {
				
				request.addAttributes(attr);
				
			}
			
		//	request.addAttributes("*");
			request.addAttributes("+");

			SearchCursor searchCursor = connection.search(request);
			
			while (searchCursor.next()) {
				Response response = searchCursor.get();
				attrs = new HashMap<String, String>();
				if (response instanceof SearchResultEntry) {
					
					Entry entry = ((SearchResultEntry) response).getEntry();
					
//					if (returningAttributes != null) {
//						for (String attr : returningAttributes) {
//							attrs.put(attr, entry.get(attr) != null ? entry.get(attr).getString() : "");
//						}
//					}
					
					for (Iterator iterator = entry.getAttributes().iterator(); iterator.hasNext();) {
						Attribute attr = (Attribute) iterator.next();
						String attrName= attr.getUpId();
						String value=attr.get().getString();
						
						attrs.put(attrName, value);
						
					}
					
					LdapEntry ldapEntry= new LdapEntry(entry.getDn().toString(), attrs, convertObjectClass2DNType(entry.get("objectClass")));
					
					//ldapEntry.setParent(dn);
					
					ldapEntry.setEntryUUID(ldapEntry.getAttributes().get("entryUUID"));
					ldapEntry.setHasSubordinates(ldapEntry.getAttributes().get("hasSubordinates"));
					
					ldapEntry.setOu(ldapEntry.getAttributes().get("ou"));
					ldapEntry.setCn(ldapEntry.getAttributes().get("cn"));
					ldapEntry.setSn(ldapEntry.getAttributes().get("sn"));
					ldapEntry.setUid(ldapEntry.getAttributes().get("uid"));
					ldapEntry.setO(ldapEntry.getAttributes().get("o"));
					ldapEntry.setUserPassword(ldapEntry.getAttributes().get("userPassword"));
					
					ldapEntry.setName( (ldapEntry.getAttributes().get("ou")!=null &&  !ldapEntry.getAttributes().get("ou").equals("")) 
							? ldapEntry.getAttributes().get("ou") : ldapEntry.getAttributes().get("cn")!=null &&  !ldapEntry.getAttributes().get("cn").equals("") 
							? ldapEntry.getAttributes().get("cn") : ldapEntry.getAttributes().get("o") );
					
					result.add(ldapEntry);
//					if("TRUE".equals(ldapEntry.getHasSubordinates())){
//						List<LdapEntry> entries=findSubEntries(ldapEntry.getDistinguishedName(),"(objectclass=organizationalUnit)",
//								new String[]{"cn","entryUUID","hasSubordinates"}, SearchScope.ONELEVEL);
//						
//					}
					
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new LdapException(e);
		} finally {
			releaseConnection(connection);
		}

		return result;
	}
	
	
	
	public LdapEntry getLdapTree(LdapEntry ldapEntry)  {
		
		if(ldapEntry.getChildEntries()!=null){
			return ldapEntry;
		}
		else{
			try {
				List<LdapEntry> entries=findSubEntries(ldapEntry.getDistinguishedName(),"(objectclass=organizationalUnit)",
						new String[]{"*"}, SearchScope.ONELEVEL);
				
				ldapEntry.setChildEntries(entries);
				for (LdapEntry ldapEntry2 : entries) {
					ldapEntry2.setParent(ldapEntry.getEntryUUID());
					getLdapTree(ldapEntry2);
				}

				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return null;
		
	}
	
	
	
	@Override
	public LdapEntry getDomainEntry() throws LdapException {
		
		LdapEntry domainEntry= null;
			
		List<LdapEntry> entries = findSubEntries(configurationService.getLdapRootDn(), "(objectclass=*)", new String[]{"*"}, SearchScope.OBJECT);
			
		if(entries.size()>0) domainEntry=entries.get(0);

		return domainEntry;
	}
	

	@Override
	public boolean isAhenk(LdapEntry entry) {
		return entry.getType() == DNType.AHENK;
	}

	@Override
	public boolean isUser(LdapEntry entry) {
		return entry.getType() == DNType.USER;
	}

	/**
	 * Find target entries which subject to command execution from provided DN
	 * list.
	 * 
	 * @param dnList
	 *            a collection of DN strings. Each DN may point to AGENT, USER,
	 *            GROUP or ORGANIZATIONAL_UNIT
	 * @param dnType
	 *            indicates which types to search for. (possible values: AGENT,
	 *            USER, GROUP, ALL)
	 * @return
	 */
	@Override
	public List<LdapEntry> findTargetEntries(List<String> dnList, DNType dnType) {
		List<LdapEntry> entries = null;
		if (dnList != null && !dnList.isEmpty() && dnType != null) {
			// Determine returning attributes
			// User LDAP privilege is used during authorization and agent JID
			// attribute is used during task execution
			String[] returningAttributes = new String[] { configurationService.getUserLdapPrivilegeAttribute(),
					configurationService.getAgentLdapJidAttribute() };
			if (configurationService.getLdapMailNotifierAttributes() != null) {
				Set<String> attrs = new HashSet<String>();
				attrs.add(configurationService.getUserLdapPrivilegeAttribute());
				attrs.add(configurationService.getAgentLdapJidAttribute());
				String[] attrArr = configurationService.getLdapMailNotifierAttributes().split(",");
				for (String attr : attrArr) {
					attrs.add(attr.trim());
				}
				returningAttributes = attrs.toArray(new String[attrs.size()]);
			}

			// Construct filtering attributes
			String objectClasses = convertDNType2ObjectClass(dnType);
			logger.debug("Object classes: {}", objectClasses);
			List<LdapSearchFilterAttribute> filterAttributes = new ArrayList<LdapSearchFilterAttribute>();
			// There may be multiple object classes
			String[] objectClsArr = objectClasses.split(",");
			for (String objectClass : objectClsArr) {
				LdapSearchFilterAttribute fAttr = new LdapSearchFilterAttribute("objectClass", objectClass,
						SearchFilterEnum.EQ);
				filterAttributes.add(fAttr);
			}
			logger.debug("Filtering attributes: {}", filterAttributes);

			entries = new ArrayList<LdapEntry>();

			// For each DN, find its target (child) entries according to desired
			// DN type:
			for (String dn : dnList) {
				try {
					List<LdapEntry> result = this.search(dn, filterAttributes, returningAttributes);
					if (result != null && !result.isEmpty()) {
						for (LdapEntry entry : result) {
							if (isValidType(entry.getType(), dnType)) {
								entries.add(entry);
							}
						}
					}
				} catch (LdapException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		logger.debug("Target entries: {}", entries);
		return entries;
	}

	/**
	 * 
	 * @param type
	 * @param desiredType
	 *            possible values: AGENT, USER, GROUP, ALL
	 * @return true if provided type is desired type (or its child), false
	 *         otherwise.
	 */
	private boolean isValidType(DNType type, DNType desiredType) {
		return type == desiredType
				|| (desiredType == DNType.ALL && (type == DNType.AHENK || type == DNType.USER || type == DNType.GROUP));
	}

	/**
	 * Determine and return object classes to be used according to provided DN
	 * type.
	 * 
	 * @param dnType
	 * @return
	 */
	private String convertDNType2ObjectClass(DNType dnType) {
		if (DNType.AHENK == dnType) {
			return configurationService.getAgentLdapObjectClasses();
		} else if (DNType.USER == dnType) {
			return configurationService.getUserLdapObjectClasses();
		} else if (DNType.GROUP == dnType) {
			return configurationService.getGroupLdapObjectClasses();
		} else if (DNType.ALL == dnType) {
			return "*";
		} else {
			throw new IllegalArgumentException("DN type was invalid.");
		}
	}

	/**
	 * Determine DN type for given objectClass attribute
	 * 
	 * @param attribute
	 * @return
	 */
	private DNType convertObjectClass2DNType(Attribute objectClass) {
		if(objectClass== null) return null;
		// Check if agent
		String agentObjectClasses = configurationService.getAgentLdapObjectClasses();
		boolean isAgent = objectClass.contains(agentObjectClasses.split(","));
		if (isAgent) {
			return DNType.AHENK;
		}
		// Check if user
		String userObjectClasses = configurationService.getUserLdapObjectClasses();
		boolean isUser = objectClass.contains(userObjectClasses.split(","));
		if (isUser) {
			return DNType.USER;
		}
		// Check if group
		String groupObjectClasses = configurationService.getGroupLdapObjectClasses();
		boolean isGroup = objectClass.contains(groupObjectClasses.split(","));
		if (isGroup) {
			return DNType.GROUP;
		}
		boolean isOrganizationalGroup = objectClass.contains("organizationalUnit");
		if (isOrganizationalGroup) {
			return DNType.ORGANIZATIONAL_UNIT;
		}
		return null;
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
	 * @param cacheService
	 */
	public void setCacheService(ICacheService cacheService) {
		this.cacheService = cacheService;
	}

}
