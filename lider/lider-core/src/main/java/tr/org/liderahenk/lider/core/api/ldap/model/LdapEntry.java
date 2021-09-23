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
package tr.org.liderahenk.lider.core.api.ldap.model;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * LDAP entry mapping
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LdapEntry {

	/**
	 * distinguished name
	 */
	private String distinguishedName;
	
	private String ou;
	
	private String cn;
	
	private String uid;
	
	private String sn;
	
	private String o;
	
	private String userPassword;
	
	private String parent;
	
	private String entryUUID;
	
	private String hasSubordinates;

	private String name;	
	/**
	 * single valued attributes
	 */
	private Map<String, String> attributes;

	private DNType type;
	
	private boolean isOnline;
	
	private List<LdapEntry> childEntries;

	/**
	 * 
	 * @param dn
	 * @param attributes
	 * @param type
	 */
	public LdapEntry(String dn, Map<String, String> attributes, DNType type) {
		this.distinguishedName = dn;
		this.attributes = attributes;
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public String getDistinguishedName() {
		return distinguishedName;
	}

	/**
	 * 
	 * @return attribute name/value
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * 
	 * @param attribute
	 * @return attribute value
	 */
	public String get(String attribute) {
		return getAttributes().get(attribute);
	}

	/**
	 * 
	 * @return DN type
	 */
	public DNType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "LdapEntry [distinguishedName=" + distinguishedName + ", attributes=" + attributes + ", type=" + type
				+ "]";
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setType(DNType type) {
		this.type = type;
	}

	public String getEntryUUID() {
		return entryUUID;
	}

	public void setEntryUUID(String entryUUID) {
		this.entryUUID = entryUUID;
	}

	public String getHasSubordinates() {
		return hasSubordinates;
	}

	public void setHasSubordinates(String hasSubordinates) {
		this.hasSubordinates = hasSubordinates;
	}

	public List<LdapEntry> getChildEntries() {
		return childEntries;
	}

	public void setChildEntries(List<LdapEntry> childEntries) {
		this.childEntries = childEntries;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}
