package tr.org.liderahenk.utils;

import tr.org.liderahenk.enums.SearchFilterEnum;

/**
 * This class is used to filter LDAP entries during search operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.ldap.ILDAPService
 * @see tr.org.liderahenk.lider.impl.ldap.LDAPServiceImpl
 *
 */
public class LdapSearchFilterAttribute {

	private String attributeName;
	private String attributeValue;
	private SearchFilterEnum operator;

	public LdapSearchFilterAttribute() {
	}

	public LdapSearchFilterAttribute(String attributeName, String attributeValue, SearchFilterEnum operator) {
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.operator = operator;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public SearchFilterEnum getOperator() {
		return operator;
	}

	public void setOperator(SearchFilterEnum operator) {
		this.operator = operator;
	}

}
