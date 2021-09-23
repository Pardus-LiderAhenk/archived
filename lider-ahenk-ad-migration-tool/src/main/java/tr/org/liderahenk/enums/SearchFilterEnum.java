package tr.org.liderahenk.enums;

/**
 * Enum class for filtering attributes.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute
 *
 */
public enum SearchFilterEnum {

	EQ("="), NOT_EQ("!=");

	private String operator;

	SearchFilterEnum(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

}
