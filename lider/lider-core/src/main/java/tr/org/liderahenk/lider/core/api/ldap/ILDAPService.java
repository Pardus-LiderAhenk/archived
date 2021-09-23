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
package tr.org.liderahenk.lider.core.api.ldap;

import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;

import tr.org.liderahenk.lider.core.api.ldap.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * Provides LDAP backend services
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ILDAPService {

	LdapConnection getConnection() throws LdapException;

	void releaseConnection(LdapConnection ldapConnection);

	IUser getUser(String userDN) throws LdapException;

	void addEntry(String newDn, Map<String, String[]> attributes) throws LdapException;

	void deleteEntry(String dn) throws LdapException;

	void updateEntry(String entryDn, String attribute, String value) throws LdapException;

	void updateEntryAddAtribute(String entryDn, String attribute, String value) throws LdapException;

	void updateEntryRemoveAttribute(String entryDn, String attribute) throws LdapException;

	void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value) throws LdapException;

	Entry getRootDSE() throws LdapException;

	LdapEntry getEntry(String entryDn, String[] requestedAttributes) throws LdapException;

	String getDN(String baseDn, String attributeName, String attributeValue) throws LdapException;

	List<LdapEntry> search(String baseDn, List<LdapSearchFilterAttribute> filterAttributes,
			String[] returningAttributes) throws LdapException;

	List<LdapEntry> search(List<LdapSearchFilterAttribute> filterAttributes, String[] returningAttributes)
			throws LdapException;

	List<LdapEntry> search(String attributeName, String attributeValue, String[] returningAttributes)
			throws LdapException;

	
	List<LdapEntry> findSubEntries(String dn, String filter, String[] returningAttributes, SearchScope scope) throws LdapException;
	
	LdapEntry getLdapTree(LdapEntry ldapEntry);
	
	boolean isAhenk(LdapEntry entry);

	boolean isUser(LdapEntry entry);

	List<LdapEntry> findTargetEntries(List<String> dnList, DNType dnType);
	LdapEntry getDomainEntry() throws LdapException;

	void renameEntry(String oldName, String newName) throws LdapException;

	void moveEntry(String entryDn, String newSuperiorDn) throws LdapException;

}
