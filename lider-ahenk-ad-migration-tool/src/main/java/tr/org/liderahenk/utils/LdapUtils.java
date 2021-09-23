package tr.org.liderahenk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
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
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUtils {

	private final static Logger logger = LoggerFactory.getLogger(LdapUtils.class);

	private LdapConnectionPool pool;

	public LdapUtils(String host, int port, String username, String password, boolean useSsl) {
		// Configure
		LdapConnectionConfig config = new LdapConnectionConfig();
		config.setLdapHost(host);
		config.setLdapPort(port);
		config.setName(username);
		config.setCredentials(password);
		config.setUseSsl(useSsl);
		// Create connection factory
		DefaultPoolableLdapConnectionFactory factory = new DefaultPoolableLdapConnectionFactory(config);
		pool = new LdapConnectionPool(factory);
		pool.setTestOnBorrow(true);
	}

	public void destroy() {
		logger.info("Closing connection pool.");
		try {
			pool.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Create new LDAP entry
	 */
	public void addEntry(String newDn, Map<String, String[]> attributes) throws Exception {

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
				throw new LdapException(
						ldapResult.getDiagnosticMessage() + " " + ldapResult.getResultCode().toString());
			}
		} finally {
			releaseConnection(connection);
		}
	}

	/**
	 * Delete specified LDAP entry
	 * 
	 * @param dn
	 * @throws Exception
	 */
	public void deleteEntry(String dn) throws Exception {
		LdapConnection connection = getConnection();
		try {
			connection.delete(new Dn(dn));
		} finally {
			releaseConnection(connection);
		}
	}

	public void updateEntry(String entryDn, String attribute, String value) throws Exception {
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
		} finally {
			releaseConnection(connection);
		}
	}

	public void updateEntryAddAtribute(String entryDn, String attribute, String value) throws Exception {
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
		} finally {
			releaseConnection(connection);
		}
	}

	public void updateEntryRemoveAttribute(String entryDn, String attribute) throws Exception {

		logger.info("Removing attribute: {}", attribute);
		LdapConnection connection = null;

		connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.lookup(entryDn);
			if (entry != null) {

				for (Attribute a : entry.getAttributes()) {

					if (a.getAttributeType().getName().equalsIgnoreCase("owner")) {
						entry.remove(a);
					}
				}

				connection.modify(entry, ModificationOperation.REMOVE_ATTRIBUTE);
			}
		} finally {
			releaseConnection(connection);
		}
	}

	public void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value) throws Exception {

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

				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);
			}
		} finally {
			releaseConnection(connection);
		}

	}

	/**
	 * @return LDAP root DN
	 */
	public Entry getRootDSE() throws Exception {
		LdapConnection connection = getConnection();
		Entry entry = null;
		try {
			entry = connection.getRootDse();
		} finally {
			releaseConnection(connection);
		}
		return entry;
	}

	public Entry findEntry(String dn) throws Exception {

		LdapConnection connection = null;
		Entry entry = null;

		if (dn == null || dn.isEmpty()) {
			throw new IllegalArgumentException("DN was null!");
		}

		try {
			connection = getConnection();
			entry = connection.lookup(dn);
		} finally {
			releaseConnection(connection);
		}

		return entry;
	}

	/**
	 * Main search method for LDAP entries.
	 * 
	 * @param baseDn
	 * @param filterAttributes
	 * @param returningAttributes
	 * @return
	 * @throws Exception
	 */
	public List<Entry> search(String baseDn, List<LdapSearchFilterAttribute> filterAttributes,
			String[] returningAttributes) throws Exception {
		List<Entry> result = new ArrayList<Entry>();

		LdapConnection connection = null;

		try {
			connection = getConnection();

			SearchRequest req = new SearchRequestImpl();
			req.setScope(SearchScope.SUBTREE);

			if (returningAttributes != null && returningAttributes.length > 0) {
				req.addAttributes(returningAttributes);
			} else {
				req.addAttributes("*");
			}

			// Construct filter expression
			String searchFilterStr = "(&";
			for (LdapSearchFilterAttribute filterAttr : filterAttributes) {
				searchFilterStr = searchFilterStr + "(" + filterAttr.getAttributeName()
						+ filterAttr.getOperator().getOperator() + filterAttr.getAttributeValue() + ")";
			}
			searchFilterStr = searchFilterStr + ")";
			req.setFilter(searchFilterStr);

			req.setTimeLimit(0);
			req.setBase(new Dn(baseDn));

			SearchCursor searchCursor = connection.search(req);
			while (searchCursor.next()) {
				Response response = searchCursor.get();
				if (response instanceof SearchResultEntry) {
					result.add(((SearchResultEntry) response).getEntry());
				}
			}
		} finally {
			releaseConnection(connection);
		}

		return result;
	}

	/**
	 * 
	 * @return new LDAP connection
	 * @throws Exception
	 */
	private LdapConnection getConnection() throws Exception {
		logger.info("Opening connection.");
		LdapConnection connection = null;
		connection = pool.getConnection();
		return connection;
	}

	/**
	 * Try to release specified connection
	 * 
	 * @param ldapConnection
	 */
	private void releaseConnection(LdapConnection ldapConnection) {
		logger.info("Closing connection.");
		try {
			pool.releaseConnection(ldapConnection);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}