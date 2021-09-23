package tr.org.liderahenk.admigration.config;

/**
 * Contains configuration variables used for AD-LDAP migration
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class MigrationConfig {

	private String adHost;

	private int adPort;

	private String adUsername;

	private String adPassword;

	private String adUserSearchBaseDn;

	private String[] adUserObjectClasses;

	private String adGroupSearchBaseDn;

	private String[] adGroupObjectClasses;

	private String ldapHost;

	private int ldapPort;

	private String ldapUsername;

	private String ldapPassword;

	private String ldapUserSearchBaseDn;

	private String[] ldapUserObjectClasses;

	private String ldapGroupSearchBaseDn;

	private String[] ldapGroupObjectClasses;

	private String ldapNewUserEntrySuffix;

	private String ldapNewUserEntryPrefixAttr;

	private String ldapNewGroupEntrySuffix;

	private String ldapNewGroupEntryPrefixAttr;

	/**
	 * This variable is used to enable/disable finish button
	 */
	private boolean isInstallationFinished = false;

	public String getAdHost() {
		return adHost;
	}

	public void setAdHost(String adHost) {
		this.adHost = adHost;
	}

	public int getAdPort() {
		return adPort;
	}

	public void setAdPort(int adPort) {
		this.adPort = adPort;
	}

	public String getAdUsername() {
		return adUsername;
	}

	public void setAdUsername(String adUsername) {
		this.adUsername = adUsername;
	}

	public String getAdPassword() {
		return adPassword;
	}

	public void setAdPassword(String adPassword) {
		this.adPassword = adPassword;
	}

	public String getAdUserSearchBaseDn() {
		return adUserSearchBaseDn;
	}

	public void setAdUserSearchBaseDn(String adUserSearchBaseDn) {
		this.adUserSearchBaseDn = adUserSearchBaseDn;
	}

	public String[] getAdUserObjectClasses() {
		return adUserObjectClasses;
	}

	public void setAdUserObjectClasses(String[] adUserObjectClasses) {
		this.adUserObjectClasses = adUserObjectClasses;
	}

	public String getAdGroupSearchBaseDn() {
		return adGroupSearchBaseDn;
	}

	public void setAdGroupSearchBaseDn(String adGroupSearchBaseDn) {
		this.adGroupSearchBaseDn = adGroupSearchBaseDn;
	}

	public String[] getAdGroupObjectClasses() {
		return adGroupObjectClasses;
	}

	public void setAdGroupObjectClasses(String[] adGroupObjectClasses) {
		this.adGroupObjectClasses = adGroupObjectClasses;
	}

	public String getLdapHost() {
		return ldapHost;
	}

	public void setLdapHost(String ldapHost) {
		this.ldapHost = ldapHost;
	}

	public int getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(int ldapPort) {
		this.ldapPort = ldapPort;
	}

	public String getLdapUsername() {
		return ldapUsername;
	}

	public void setLdapUsername(String ldapUsername) {
		this.ldapUsername = ldapUsername;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getLdapUserSearchBaseDn() {
		return ldapUserSearchBaseDn;
	}

	public void setLdapUserSearchBaseDn(String ldapUserSearchBaseDn) {
		this.ldapUserSearchBaseDn = ldapUserSearchBaseDn;
	}

	public String[] getLdapUserObjectClasses() {
		return ldapUserObjectClasses;
	}

	public void setLdapUserObjectClasses(String[] ldapUserObjectClasses) {
		this.ldapUserObjectClasses = ldapUserObjectClasses;
	}

	public String getLdapGroupSearchBaseDn() {
		return ldapGroupSearchBaseDn;
	}

	public void setLdapGroupSearchBaseDn(String ldapGroupSearchBaseDn) {
		this.ldapGroupSearchBaseDn = ldapGroupSearchBaseDn;
	}

	public String[] getLdapGroupObjectClasses() {
		return ldapGroupObjectClasses;
	}

	public void setLdapGroupObjectClasses(String[] ldapGroupObjectClasses) {
		this.ldapGroupObjectClasses = ldapGroupObjectClasses;
	}

	public String getLdapNewUserEntrySuffix() {
		return ldapNewUserEntrySuffix;
	}

	public void setLdapNewUserEntrySuffix(String ldapNewUserEntrySuffix) {
		this.ldapNewUserEntrySuffix = ldapNewUserEntrySuffix;
	}

	public String getLdapNewUserEntryPrefixAttr() {
		return ldapNewUserEntryPrefixAttr;
	}

	public void setLdapNewUserEntryPrefixAttr(String ldapNewUserEntryPrefixAttr) {
		this.ldapNewUserEntryPrefixAttr = ldapNewUserEntryPrefixAttr;
	}

	public String getLdapNewGroupEntrySuffix() {
		return ldapNewGroupEntrySuffix;
	}

	public void setLdapNewGroupEntrySuffix(String ldapNewGroupEntrySuffix) {
		this.ldapNewGroupEntrySuffix = ldapNewGroupEntrySuffix;
	}

	public String getLdapNewGroupEntryPrefixAttr() {
		return ldapNewGroupEntryPrefixAttr;
	}

	public void setLdapNewGroupEntryPrefixAttr(String ldapNewGroupEntryPrefixAttr) {
		this.ldapNewGroupEntryPrefixAttr = ldapNewGroupEntryPrefixAttr;
	}

	public boolean isInstallationFinished() {
		return isInstallationFinished;
	}

	public void setInstallationFinished(boolean isInstallationFinished) {
		this.isInstallationFinished = isInstallationFinished;
	}

}
