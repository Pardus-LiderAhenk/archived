package tr.org.liderahenk.installer.lider.config;

import java.util.Map;

import tr.org.liderahenk.installer.lider.wizard.model.DatabaseNodeInfoModel;
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseNodeSwtModel;
import tr.org.liderahenk.installer.lider.wizard.model.LiderNodeInfoModel;
import tr.org.liderahenk.installer.lider.wizard.model.XmppNodeInfoModel;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 * 
 *         Contains configuration variables used throughout the whole setup
 *         process.
 */
public class LiderSetupConfig {

	/**
	 * ###################################
	 * 
	 * Database Configuration
	 * 
	 * ###################################
	 */

	/**
	 * Indicates whether to install database server
	 */
	private boolean installDatabase;

	/**
	 * Package name to be installed as database server
	 */
	private String databasePackageName;

	/**
	 * Root password for the database server
	 */
	private String databaseRootPassword;

	/**
	 * Database server IP
	 */
	private String databaseIp;

	/**
	 * Port number which is used for SSH connection
	 */
	private Integer databasePort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod databaseAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String databaseAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String databaseAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String databaseAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String databaseAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod databaseInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String databaseDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] databaseDebFileContent;

	private String databaseDownloadUrl;
	
	private boolean databaseCluster;
	
	private String databaseClusterAddress;

	private String databaseClusterAddressForLider;
	
	private String databaseClusterName;

	private String databaseSstUsername;
	
	private String databaseSstPwd;
	
	private Map<Integer, DatabaseNodeSwtModel> databaseNodeMap; 

	private Map<Integer, DatabaseNodeInfoModel> databaseNodeInfoMap; 

	/**
	 * ###################################
	 * 
	 * LDAP Configuration
	 *
	 * ###################################
	 */

	/**
	 * Indicates whether to install LDAP server
	 */
	private boolean installLdap;

	/**
	 * Package name to be installed as LDAP server
	 */
	private String ldapPackageName;

	/**
	 * Root password for the LDAP server
	 */
	private String ldapRootPassword;

	/**
	 * LDAP server IP
	 */
	private String ldapIp;

	/**
	 * SSH port which is used to connect to server
	 */
	private Integer ldapPort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod ldapAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String ldapAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String ldapAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String ldapAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String ldapAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod ldapInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String ldapDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] ldapDebFileContent;

	/**
	 * LDAP configuration file content
	 */
	private String ldapConfContent;

	private String ldapDownloadUrl;
	
	private String ldapBaseDn;

	private String ldapAdminCn;

	private String ldapAdminCnPwd;
	
	private String ldapOrgName;
	
	private String ldapOrgCn;
	
	private String ldapBaseCn;
	
	private String ldapAdminDn;

	private String ldapAdminDnPwd;
	
	private boolean ldapUpdate;
	
	/**
	 * Absolute path of LDAP configuration file.
	 */
	private String ldapAbsPathConfFile;

	/**
	 * ###################################
	 * 
	 * XMPP Configuration
	 * 
	 * ###################################
	 */

	/**
	 * Indicates whether to install XMPP Server
	 */
	private boolean installXmpp;

	/**
	 * Package name to be installed as XMPP server
	 */
	private String xmppPackageName;

	/**
	 * XMPP server IP
	 */
	private String xmppIp;

	/**
	 * SSH port which is used to connect to server
	 */
	private Integer xmppPort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod xmppAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String xmppAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String xmppAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String xmppAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String xmppAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod xmppInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String xmppDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] xmppDebFileContent;

	private String xmppDownloadUrl;

	/**
	 * XMPP hostname
	 */
	private String xmppHostname;

	/**
	 * Password for user: admin in XMPP
	 */
	private String xmppAdminPwd;

	/**
	 * Username for Lider Server user in XMPP.
	 */
	private String xmppLiderUsername;

	/**
	 * Password for Lider Server user in XMPP.
	 */
	private String xmppLiderPassword;

	/**
	 * XMPP configuration file content
	 */
	private String xmppConfContent;

	/**
	 * Absolute path of XMPP configuration file.
	 */
	private String xmppAbsPathConfFile;

	private Map<Integer, XmppNodeInfoModel> xmppNodeInfoMap;

	private boolean xmppCluster;
	private String xmppLdapServerAddress;
	private String xmppLdapRootDn;
	private String xmppLdapRootPwd;
	private String xmppLdapBaseDn;
	private String xmppProxyAddress;
	private String xmppProxyPwd;
	
	
	/**
	 * ###################################
	 * 
	 * Lider Configuration
	 * 
	 * ###################################
	 */

	/**
	 * Indicates whether to install Lider Server
	 */
	private boolean installLider;

	/**
	 * Package name to be installed as Lider server
	 */
	private String liderPackageName;

	/**
	 * Lider server IP
	 */
	private String liderIp;

	/**
	 * SSH port which is used to connect to server
	 */
	private Integer liderPort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod liderAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String liderAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String liderAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String liderAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String liderAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod liderInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String liderDebFileName;

	/**
	 * File name of the provided TAR file
	 */
	private String liderTarFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] liderDebFileContent;

	/**
	 * File content of the provided TAR file
	 */
	private byte[] liderTarFileContent;

	private String liderDownloadUrl;

	/**
	 * Lider configuration file content
	 */
	private String liderConfContent;

	private String liderAbsPathConfFile;

	private String datasourceConfContent;

	private String datasourceAbsPathConfFile;

	private String liderProxyPwd;
	
	private String liderLdapServerAddress;
	private Integer liderLdapPort;
	private String liderLdapAdminUser;
	private String liderLdapAdminPwd;
	private String liderLdapBaseDn;
	private String liderLdapUseSsl;
	
	private String liderXmppAddress;
	private Integer liderXmppPort;
	private String liderXmppLiderUser;
	private String liderXmppLiderPwd;
	private String liderXmppServiceName;
	private String liderXmppMaxTrials;
	private String liderXmppPacketTimeout;
	private String liderXmppPingTimeout;
	private String liderXmppUseSsl;
	
	private String liderDbAddress;
	private Integer liderDbPort;
	private String liderDbName;
	private String liderDbUsername;
	private String liderDbPwd;

	private String liderAgentLdapBaseDn;
	private String liderAgentLdapIdAttribute;
	private String liderAgentLdapJidAttribute;
	private String liderAgentLdapClasses;
	
	private String liderUserLdapBaseDn;
	private String liderUserLdapIdAttribute;
	private String liderUserLdapPrivilegeAttribute;
	private String liderUserLdapClasses;
	private String liderUserGroupLdapClasses;
	
	private String liderFileServerProtocol;
	private String liderFileServerHost;
	private String liderFileServerPort;
	private String liderFileServerUsername;
	private String liderFileServerPwd;
	private String liderFileServerPluginPath;
	private String liderFileServerAgreementPath;
	private String liderFileServerAgentFilePath;
	
	private String liderProxyAddress;
	private Map<Integer, LiderNodeInfoModel> liderNodeInfoMap;
	
	private boolean isLiderCluster;
	
	/**
	 * This variable is used to enable/disable finish button
	 */
	private boolean isInstallationFinished = false;

	/**
	 * ###################################
	 * 
	 * Getter/setter
	 * 
	 * ###################################
	 */

	public boolean isInstallDatabase() {
		return installDatabase;
	}

	public void setInstallDatabase(boolean installDatabase) {
		this.installDatabase = installDatabase;
	}

	public boolean isInstallLdap() {
		return installLdap;
	}

	public void setInstallLdap(boolean installLdap) {
		this.installLdap = installLdap;
	}

	public boolean isInstallXmpp() {
		return installXmpp;
	}

	public void setInstallXmpp(boolean installXmpp) {
		this.installXmpp = installXmpp;
	}

	public boolean isInstallLider() {
		return installLider;
	}

	public void setInstallLider(boolean installLider) {
		this.installLider = installLider;
	}

	public String getDatabaseIp() {
		return databaseIp;
	}

	public void setDatabaseIp(String databaseIp) {
		this.databaseIp = databaseIp;
	}

	public String getLdapIp() {
		return ldapIp;
	}

	public void setLdapIp(String ldapIp) {
		this.ldapIp = ldapIp;
	}

	public String getXmppIp() {
		return xmppIp;
	}

	public void setXmppIp(String xmppIp) {
		this.xmppIp = xmppIp;
	}

	public String getLiderIp() {
		return liderIp;
	}

	public void setLiderIp(String liderIp) {
		this.liderIp = liderIp;
	}

	public String getDatabaseAccessUsername() {
		return databaseAccessUsername;
	}

	public void setDatabaseAccessUsername(String databaseAccessUsername) {
		this.databaseAccessUsername = databaseAccessUsername;
	}

	public String getDatabaseAccessPasswd() {
		return databaseAccessPasswd;
	}

	public void setDatabaseAccessPasswd(String databaseAccessPasswd) {
		this.databaseAccessPasswd = databaseAccessPasswd;
	}

	public String getDatabaseAccessKeyPath() {
		return databaseAccessKeyPath;
	}

	public void setDatabaseAccessKeyPath(String databaseAccessKeyPath) {
		this.databaseAccessKeyPath = databaseAccessKeyPath;
	}

	public String getDatabaseAccessPassphrase() {
		return databaseAccessPassphrase;
	}

	public void setDatabaseAccessPassphrase(String databaseAccessPassphrase) {
		this.databaseAccessPassphrase = databaseAccessPassphrase;
	}

	public InstallMethod getDatabaseInstallMethod() {
		return databaseInstallMethod;
	}

	public void setDatabaseInstallMethod(InstallMethod databaseInstallMethod) {
		this.databaseInstallMethod = databaseInstallMethod;
	}

	public AccessMethod getDatabaseAccessMethod() {
		return databaseAccessMethod;
	}

	public void setDatabaseAccessMethod(AccessMethod databaseAccessMethod) {
		this.databaseAccessMethod = databaseAccessMethod;
	}

	public String getDatabasePackageName() {
		return databasePackageName;
	}

	public void setDatabasePackageName(String databasePackageName) {
		this.databasePackageName = databasePackageName;
	}

	public String getDatabaseRootPassword() {
		return databaseRootPassword;
	}

	public void setDatabaseRootPassword(String databaseRootPassword) {
		this.databaseRootPassword = databaseRootPassword;
	}

	public Integer getDatabasePort() {
		return databasePort;
	}

	public void setDatabasePort(Integer databasePort) {
		this.databasePort = databasePort;
	}

	public Integer getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(Integer ldapPort) {
		this.ldapPort = ldapPort;
	}

	public Integer getXmppPort() {
		return xmppPort;
	}

	public void setXmppPort(Integer xmppPort) {
		this.xmppPort = xmppPort;
	}

	public Integer getLiderPort() {
		return liderPort;
	}

	public void setLiderPort(Integer liderPort) {
		this.liderPort = liderPort;
	}

	public String getLdapPackageName() {
		return ldapPackageName;
	}

	public void setLdapPackageName(String ldapPackageName) {
		this.ldapPackageName = ldapPackageName;
	}

	public String getLdapRootPassword() {
		return ldapRootPassword;
	}

	public void setLdapRootPassword(String ldapRootPassword) {
		this.ldapRootPassword = ldapRootPassword;
	}

	public AccessMethod getLdapAccessMethod() {
		return ldapAccessMethod;
	}

	public void setLdapAccessMethod(AccessMethod ldapAccessMethod) {
		this.ldapAccessMethod = ldapAccessMethod;
	}

	public String getLdapAccessUsername() {
		return ldapAccessUsername;
	}

	public void setLdapAccessUsername(String ldapAccessUsername) {
		this.ldapAccessUsername = ldapAccessUsername;
	}

	public String getLdapAccessPasswd() {
		return ldapAccessPasswd;
	}

	public void setLdapAccessPasswd(String ldapAccessPasswd) {
		this.ldapAccessPasswd = ldapAccessPasswd;
	}

	public String getLdapAccessKeyPath() {
		return ldapAccessKeyPath;
	}

	public void setLdapAccessKeyPath(String ldapAccessKeyPath) {
		this.ldapAccessKeyPath = ldapAccessKeyPath;
	}

	public String getLdapAccessPassphrase() {
		return ldapAccessPassphrase;
	}

	public void setLdapAccessPassphrase(String ldapAccessPassphrase) {
		this.ldapAccessPassphrase = ldapAccessPassphrase;
	}

	public String getDatabaseDebFileName() {
		return databaseDebFileName;
	}

	public void setDatabaseDebFileName(String databaseDebFileName) {
		this.databaseDebFileName = databaseDebFileName;
	}

	public byte[] getDatabaseDebFileContent() {
		return databaseDebFileContent;
	}

	public void setDatabaseDebFileContent(byte[] databaseDebFileContent) {
		this.databaseDebFileContent = databaseDebFileContent;
	}

	public String getLdapDebFileName() {
		return ldapDebFileName;
	}

	public void setLdapDebFileName(String ldapDebFileName) {
		this.ldapDebFileName = ldapDebFileName;
	}

	public byte[] getLdapDebFileContent() {
		return ldapDebFileContent;
	}

	public void setLdapDebFileContent(byte[] ldapDebFileContent) {
		this.ldapDebFileContent = ldapDebFileContent;
	}

	public InstallMethod getLdapInstallMethod() {
		return ldapInstallMethod;
	}

	public void setLdapInstallMethod(InstallMethod ldapInstallMethod) {
		this.ldapInstallMethod = ldapInstallMethod;
	}

	public AccessMethod getXmppAccessMethod() {
		return xmppAccessMethod;
	}

	public void setXmppAccessMethod(AccessMethod xmppAccessMethod) {
		this.xmppAccessMethod = xmppAccessMethod;
	}

	public String getXmppAccessUsername() {
		return xmppAccessUsername;
	}

	public void setXmppAccessUsername(String xmppAccessUsername) {
		this.xmppAccessUsername = xmppAccessUsername;
	}

	public String getXmppAccessPasswd() {
		return xmppAccessPasswd;
	}

	public void setXmppAccessPasswd(String xmppAccessPasswd) {
		this.xmppAccessPasswd = xmppAccessPasswd;
	}

	public String getXmppAccessKeyPath() {
		return xmppAccessKeyPath;
	}

	public void setXmppAccessKeyPath(String xmppAccessKeyPath) {
		this.xmppAccessKeyPath = xmppAccessKeyPath;
	}

	public String getXmppAccessPassphrase() {
		return xmppAccessPassphrase;
	}

	public void setXmppAccessPassphrase(String xmppAccessPassphrase) {
		this.xmppAccessPassphrase = xmppAccessPassphrase;
	}

	public String getXmppDebFileName() {
		return xmppDebFileName;
	}

	public void setXmppDebFileName(String xmppDebFileName) {
		this.xmppDebFileName = xmppDebFileName;
	}

	public byte[] getXmppDebFileContent() {
		return xmppDebFileContent;
	}

	public void setXmppDebFileContent(byte[] xmppDebFileContent) {
		this.xmppDebFileContent = xmppDebFileContent;
	}

	public InstallMethod getXmppInstallMethod() {
		return xmppInstallMethod;
	}

	public void setXmppInstallMethod(InstallMethod xmppInstallMethod) {
		this.xmppInstallMethod = xmppInstallMethod;
	}

	public AccessMethod getLiderAccessMethod() {
		return liderAccessMethod;
	}

	public void setLiderAccessMethod(AccessMethod liderAccessMethod) {
		this.liderAccessMethod = liderAccessMethod;
	}

	public String getLiderAccessUsername() {
		return liderAccessUsername;
	}

	public void setLiderAccessUsername(String liderAccessUsername) {
		this.liderAccessUsername = liderAccessUsername;
	}

	public String getLiderAccessPasswd() {
		return liderAccessPasswd;
	}

	public void setLiderAccessPasswd(String liderAccessPasswd) {
		this.liderAccessPasswd = liderAccessPasswd;
	}

	public String getLiderAccessKeyPath() {
		return liderAccessKeyPath;
	}

	public void setLiderAccessKeyPath(String liderAccessKeyPath) {
		this.liderAccessKeyPath = liderAccessKeyPath;
	}

	public String getLiderAccessPassphrase() {
		return liderAccessPassphrase;
	}

	public void setLiderAccessPassphrase(String liderAccessPassphrase) {
		this.liderAccessPassphrase = liderAccessPassphrase;
	}

	public InstallMethod getLiderInstallMethod() {
		return liderInstallMethod;
	}

	public void setLiderInstallMethod(InstallMethod liderInstallMethod) {
		this.liderInstallMethod = liderInstallMethod;
	}

	public String getLiderDebFileName() {
		return liderDebFileName;
	}

	public void setLiderDebFileName(String liderDebFileName) {
		this.liderDebFileName = liderDebFileName;
	}

	public byte[] getLiderDebFileContent() {
		return liderDebFileContent;
	}

	public void setLiderDebFileContent(byte[] liderDebFileContent) {
		this.liderDebFileContent = liderDebFileContent;
	}

	public String getLiderPackageName() {
		return liderPackageName;
	}

	public void setLiderPackageName(String liderPackageName) {
		this.liderPackageName = liderPackageName;
	}

	public String getXmppPackageName() {
		return xmppPackageName;
	}

	public void setXmppPackageName(String xmppPackageName) {
		this.xmppPackageName = xmppPackageName;
	}

	public String getLdapConfContent() {
		return ldapConfContent;
	}

	public void setLdapConfContent(String ldapConfContent) {
		this.ldapConfContent = ldapConfContent;
	}

	public String getXmppConfContent() {
		return xmppConfContent;
	}

	public void setXmppConfContent(String xmppConfContent) {
		this.xmppConfContent = xmppConfContent;
	}

	public String getLiderConfContent() {
		return liderConfContent;
	}

	public void setLiderConfContent(String liderConfContent) {
		this.liderConfContent = liderConfContent;
	}

	public String getXmppHostname() {
		return xmppHostname;
	}

	public void setXmppHostname(String xmppHostname) {
		this.xmppHostname = xmppHostname;
	}

	public String getXmppAdminPwd() {
		return xmppAdminPwd;
	}

	public void setXmppAdminPwd(String xmppAdminPwd) {
		this.xmppAdminPwd = xmppAdminPwd;
	}

	public String getXmppLiderUsername() {
		return xmppLiderUsername;
	}

	public void setXmppLiderUsername(String xmppLiderUsername) {
		this.xmppLiderUsername = xmppLiderUsername;
	}

	public String getXmppLiderPassword() {
		return xmppLiderPassword;
	}

	public void setXmppLiderPassword(String xmppLiderPassword) {
		this.xmppLiderPassword = xmppLiderPassword;
	}

	public String getXmppAbsPathConfFile() {
		return xmppAbsPathConfFile;
	}

	public void setXmppAbsPathConfFile(String xmppAbsPathConfFile) {
		this.xmppAbsPathConfFile = xmppAbsPathConfFile;
	}

	public boolean isInstallationFinished() {
		return isInstallationFinished;
	}

	public void setInstallationFinished(boolean isInstallationFinished) {
		this.isInstallationFinished = isInstallationFinished;
	}

	public String getXmppDownloadUrl() {
		return xmppDownloadUrl;
	}

	public void setXmppDownloadUrl(String xmppDownloadUrl) {
		this.xmppDownloadUrl = xmppDownloadUrl;
	}

	public String getDatabaseDownloadUrl() {
		return databaseDownloadUrl;
	}

	public void setDatabaseDownloadUrl(String databaseDownloadUrl) {
		this.databaseDownloadUrl = databaseDownloadUrl;
	}

	public String getLdapDownloadUrl() {
		return ldapDownloadUrl;
	}

	public void setLdapDownloadUrl(String ldapDownloadUrl) {
		this.ldapDownloadUrl = ldapDownloadUrl;
	}

	public String getLiderDownloadUrl() {
		return liderDownloadUrl;
	}

	public void setLiderDownloadUrl(String liderDownloadUrl) {
		this.liderDownloadUrl = liderDownloadUrl;
	}

	public String getLiderTarFileName() {
		return liderTarFileName;
	}

	public void setLiderTarFileName(String liderTarFileName) {
		this.liderTarFileName = liderTarFileName;
	}

	public byte[] getLiderTarFileContent() {
		return liderTarFileContent;
	}

	public void setLiderTarFileContent(byte[] liderTarFileContent) {
		this.liderTarFileContent = liderTarFileContent;
	}

	public String getLdapAbsPathConfFile() {
		return ldapAbsPathConfFile;
	}

	public void setLdapAbsPathConfFile(String ldapAbsPathConfFile) {
		this.ldapAbsPathConfFile = ldapAbsPathConfFile;
	}

	public String getLiderAbsPathConfFile() {
		return liderAbsPathConfFile;
	}

	public void setLiderAbsPathConfFile(String liderAbsPathConfFile) {
		this.liderAbsPathConfFile = liderAbsPathConfFile;
	}

	public String getDatasourceConfContent() {
		return datasourceConfContent;
	}

	public void setDatasourceConfContent(String datasourceConfContent) {
		this.datasourceConfContent = datasourceConfContent;
	}

	public String getDatasourceAbsPathConfFile() {
		return datasourceAbsPathConfFile;
	}

	public void setDatasourceAbsPathConfFile(String datasourceAbsPathConfFile) {
		this.datasourceAbsPathConfFile = datasourceAbsPathConfFile;
	}

	public String getLdapBaseDn() {
		return ldapBaseDn;
	}

	public void setLdapBaseDn(String ldapBaseDn) {
		this.ldapBaseDn = ldapBaseDn;
	}

	public String getLdapAdminCn() {
		return ldapAdminCn;
	}

	public void setLdapAdminCn(String ldapAdminCn) {
		this.ldapAdminCn = ldapAdminCn;
	}

	public String getLdapAdminCnPwd() {
		return ldapAdminCnPwd;
	}

	public void setLdapAdminCnPwd(String ldapAdminCnPwd) {
		this.ldapAdminCnPwd = ldapAdminCnPwd;
	}

	public String getLdapOrgName() {
		return ldapOrgName;
	}

	public void setLdapOrgName(String ldapOrgName) {
		this.ldapOrgName = ldapOrgName;
	}

	public String getLdapOrgCn() {
		return ldapOrgCn;
	}

	public void setLdapOrgCn(String ldapOrgCn) {
		this.ldapOrgCn = ldapOrgCn;
	}

	public String getLdapBaseCn() {
		return ldapBaseCn;
	}

	public void setLdapBaseCn(String ldapBaseCn) {
		this.ldapBaseCn = ldapBaseCn;
	}

	public String getLdapAdminDn() {
		return ldapAdminDn;
	}

	public void setLdapAdminDn(String ldapAdminDn) {
		this.ldapAdminDn = ldapAdminDn;
	}

	public boolean isLdapUpdate() {
		return ldapUpdate;
	}

	public void setLdapUpdate(boolean ldapUpdate) {
		this.ldapUpdate = ldapUpdate;
	}

	public String getLdapAdminDnPwd() {
		return ldapAdminDnPwd;
	}

	public void setLdapAdminDnPwd(String ldapAdminDnPwd) {
		this.ldapAdminDnPwd = ldapAdminDnPwd;
	}

	public boolean isDatabaseCluster() {
		return databaseCluster;
	}

	public void setDatabaseCluster(boolean databaseCluster) {
		this.databaseCluster = databaseCluster;
	}

	public String getDatabaseClusterAddress() {
		return databaseClusterAddress;
	}

	public void setDatabaseClusterAddress(String databaseClusterAddress) {
		this.databaseClusterAddress = databaseClusterAddress;
	}

	public String getDatabaseClusterName() {
		return databaseClusterName;
	}

	public void setDatabaseClusterName(String databaseClusterName) {
		this.databaseClusterName = databaseClusterName;
	}

	public String getDatabaseSstUsername() {
		return databaseSstUsername;
	}

	public void setDatabaseSstUsername(String databaseSstUsername) {
		this.databaseSstUsername = databaseSstUsername;
	}

	public String getDatabaseSstPwd() {
		return databaseSstPwd;
	}

	public void setDatabaseSstPwd(String databaseSstPwd) {
		this.databaseSstPwd = databaseSstPwd;
	}

	public Map<Integer, DatabaseNodeSwtModel> getDatabaseNodeMap() {
		return databaseNodeMap;
	}

	public void setDatabaseNodeMap(Map<Integer, DatabaseNodeSwtModel> databaseNodeMap) {
		this.databaseNodeMap = databaseNodeMap;
	}

	public Map<Integer, DatabaseNodeInfoModel> getDatabaseNodeInfoMap() {
		return databaseNodeInfoMap;
	}

	public void setDatabaseNodeInfoMap(Map<Integer, DatabaseNodeInfoModel> databaseNodeInfoMap) {
		this.databaseNodeInfoMap = databaseNodeInfoMap;
	}

	public String getDatabaseClusterAddressForLider() {
		return databaseClusterAddressForLider;
	}

	public void setDatabaseClusterAddressForLider(String databaseClusterAddressForLider) {
		this.databaseClusterAddressForLider = databaseClusterAddressForLider;
	}

	public Map<Integer, XmppNodeInfoModel> getXmppNodeInfoMap() {
		return xmppNodeInfoMap;
	}

	public void setXmppNodeInfoMap(Map<Integer, XmppNodeInfoModel> xmppNodeInfoMap) {
		this.xmppNodeInfoMap = xmppNodeInfoMap;
	}

	public String getXmppLdapRootDn() {
		return xmppLdapRootDn;
	}

	public void setXmppLdapRootDn(String xmppLdapRootDn) {
		this.xmppLdapRootDn = xmppLdapRootDn;
	}

	public String getXmppLdapRootPwd() {
		return xmppLdapRootPwd;
	}

	public void setXmppLdapRootPwd(String xmppLdapRootPwd) {
		this.xmppLdapRootPwd = xmppLdapRootPwd;
	}

	public String getXmppLdapBaseDn() {
		return xmppLdapBaseDn;
	}

	public void setXmppLdapBaseDn(String xmppLdapBaseDn) {
		this.xmppLdapBaseDn = xmppLdapBaseDn;
	}

	public String getXmppProxyAddress() {
		return xmppProxyAddress;
	}

	public void setXmppProxyAddress(String xmppProxyAddress) {
		this.xmppProxyAddress = xmppProxyAddress;
	}

	public String getXmppProxyPwd() {
		return xmppProxyPwd;
	}

	public void setXmppProxyPwd(String xmppProxyPwd) {
		this.xmppProxyPwd = xmppProxyPwd;
	}

	public String getXmppLdapServerAddress() {
		return xmppLdapServerAddress;
	}

	public void setXmppLdapServerAddress(String xmppLdapServerAddress) {
		this.xmppLdapServerAddress = xmppLdapServerAddress;
	}

	public boolean isXmppCluster() {
		return xmppCluster;
	}

	public void setXmppCluster(boolean xmppCluster) {
		this.xmppCluster = xmppCluster;
	}

	public String getLiderProxyPwd() {
		return liderProxyPwd;
	}

	public void setLiderProxyPwd(String liderProxyPwd) {
		this.liderProxyPwd = liderProxyPwd;
	}

	public String getLiderLdapServerAddress() {
		return liderLdapServerAddress;
	}

	public void setLiderLdapServerAddress(String liderLdapServerAddress) {
		this.liderLdapServerAddress = liderLdapServerAddress;
	}

	public String getLiderLdapAdminUser() {
		return liderLdapAdminUser;
	}

	public void setLiderLdapAdminUser(String liderLdapAdminUser) {
		this.liderLdapAdminUser = liderLdapAdminUser;
	}

	public String getLiderLdapAdminPwd() {
		return liderLdapAdminPwd;
	}

	public void setLiderLdapAdminPwd(String liderLdapAdminPwd) {
		this.liderLdapAdminPwd = liderLdapAdminPwd;
	}

	public String getLiderLdapBaseDn() {
		return liderLdapBaseDn;
	}

	public void setLiderLdapBaseDn(String liderLdapBaseDn) {
		this.liderLdapBaseDn = liderLdapBaseDn;
	}

	public String getLiderXmppAddress() {
		return liderXmppAddress;
	}

	public void setLiderXmppAddress(String liderXmppAddress) {
		this.liderXmppAddress = liderXmppAddress;
	}

	public String getLiderXmppLiderUser() {
		return liderXmppLiderUser;
	}

	public void setLiderXmppLiderUser(String liderXmppLiderUser) {
		this.liderXmppLiderUser = liderXmppLiderUser;
	}

	public String getLiderXmppLiderPwd() {
		return liderXmppLiderPwd;
	}

	public void setLiderXmppLiderPwd(String liderXmppLiderPwd) {
		this.liderXmppLiderPwd = liderXmppLiderPwd;
	}

	public String getLiderXmppServiceName() {
		return liderXmppServiceName;
	}

	public void setLiderXmppServiceName(String liderXmppServiceName) {
		this.liderXmppServiceName = liderXmppServiceName;
	}

	public String getLiderXmppMaxTrials() {
		return liderXmppMaxTrials;
	}

	public void setLiderXmppMaxTrials(String liderXmppMaxTrials) {
		this.liderXmppMaxTrials = liderXmppMaxTrials;
	}

	public String getLiderXmppPacketTimeout() {
		return liderXmppPacketTimeout;
	}

	public void setLiderXmppPacketTimeout(String liderXmppPacketTimeout) {
		this.liderXmppPacketTimeout = liderXmppPacketTimeout;
	}

	public String getLiderXmppPingTimeout() {
		return liderXmppPingTimeout;
	}

	public void setLiderXmppPingTimeout(String liderXmppPingTimeout) {
		this.liderXmppPingTimeout = liderXmppPingTimeout;
	}

	public String getLiderDbAddress() {
		return liderDbAddress;
	}

	public void setLiderDbAddress(String liderDbAddress) {
		this.liderDbAddress = liderDbAddress;
	}

	public String getLiderDbName() {
		return liderDbName;
	}

	public void setLiderDbName(String liderDbName) {
		this.liderDbName = liderDbName;
	}

	public String getLiderDbUsername() {
		return liderDbUsername;
	}

	public void setLiderDbUsername(String liderDbUsername) {
		this.liderDbUsername = liderDbUsername;
	}

	public String getLiderDbPwd() {
		return liderDbPwd;
	}

	public void setLiderDbPwd(String liderDbPwd) {
		this.liderDbPwd = liderDbPwd;
	}

	public String getLiderAgentLdapBaseDn() {
		return liderAgentLdapBaseDn;
	}

	public void setLiderAgentLdapBaseDn(String liderAgentLdapBaseDn) {
		this.liderAgentLdapBaseDn = liderAgentLdapBaseDn;
	}

	public String getLiderAgentLdapIdAttribute() {
		return liderAgentLdapIdAttribute;
	}

	public void setLiderAgentLdapIdAttribute(String liderAgentLdapIdAttribute) {
		this.liderAgentLdapIdAttribute = liderAgentLdapIdAttribute;
	}

	public String getLiderAgentLdapJidAttribute() {
		return liderAgentLdapJidAttribute;
	}

	public void setLiderAgentLdapJidAttribute(String liderAgentLdapJidAttribute) {
		this.liderAgentLdapJidAttribute = liderAgentLdapJidAttribute;
	}

	public String getLiderAgentLdapClasses() {
		return liderAgentLdapClasses;
	}

	public void setLiderAgentLdapClasses(String liderAgentLdapClasses) {
		this.liderAgentLdapClasses = liderAgentLdapClasses;
	}

	public String getLiderUserLdapBaseDn() {
		return liderUserLdapBaseDn;
	}

	public void setLiderUserLdapBaseDn(String liderUserLdapBaseDn) {
		this.liderUserLdapBaseDn = liderUserLdapBaseDn;
	}

	public String getLiderUserLdapIdAttribute() {
		return liderUserLdapIdAttribute;
	}

	public void setLiderUserLdapIdAttribute(String liderUserLdapIdAttribute) {
		this.liderUserLdapIdAttribute = liderUserLdapIdAttribute;
	}

	public String getLiderUserLdapPrivilegeAttribute() {
		return liderUserLdapPrivilegeAttribute;
	}

	public void setLiderUserLdapPrivilegeAttribute(String liderUserLdapPrivilegeAttribute) {
		this.liderUserLdapPrivilegeAttribute = liderUserLdapPrivilegeAttribute;
	}

	public String getLiderUserLdapClasses() {
		return liderUserLdapClasses;
	}

	public void setLiderUserLdapClasses(String liderUserLdapClasses) {
		this.liderUserLdapClasses = liderUserLdapClasses;
	}

	public String getLiderUserGroupLdapClasses() {
		return liderUserGroupLdapClasses;
	}

	public void setLiderUserGroupLdapClasses(String liderUserGroupLdapClasses) {
		this.liderUserGroupLdapClasses = liderUserGroupLdapClasses;
	}

	public String getLiderProxyAddress() {
		return liderProxyAddress;
	}

	public void setLiderProxyAddress(String liderProxyAddress) {
		this.liderProxyAddress = liderProxyAddress;
	}

	public Map<Integer, LiderNodeInfoModel> getLiderNodeInfoMap() {
		return liderNodeInfoMap;
	}

	public void setLiderNodeInfoMap(Map<Integer, LiderNodeInfoModel> liderNodeInfoMap) {
		this.liderNodeInfoMap = liderNodeInfoMap;
	}

	public Integer getLiderLdapPort() {
		return liderLdapPort;
	}

	public void setLiderLdapPort(Integer liderLdapPort) {
		this.liderLdapPort = liderLdapPort;
	}

	public Integer getLiderXmppPort() {
		return liderXmppPort;
	}

	public void setLiderXmppPort(Integer liderXmppPort) {
		this.liderXmppPort = liderXmppPort;
	}

	public Integer getLiderDbPort() {
		return liderDbPort;
	}

	public void setLiderDbPort(Integer liderDbPort) {
		this.liderDbPort = liderDbPort;
	}

	public boolean isLiderCluster() {
		return isLiderCluster;
	}

	public void setLiderCluster(boolean isLiderCluster) {
		this.isLiderCluster = isLiderCluster;
	}

	public String getLiderFileServerProtocol() {
		return liderFileServerProtocol;
	}

	public void setLiderFileServerProtocol(String liderFileServerProtocol) {
		this.liderFileServerProtocol = liderFileServerProtocol;
	}

	public String getLiderFileServerHost() {
		return liderFileServerHost;
	}

	public void setLiderFileServerHost(String liderFileServerHost) {
		this.liderFileServerHost = liderFileServerHost;
	}

	public String getLiderFileServerPort() {
		return liderFileServerPort;
	}

	public void setLiderFileServerPort(String liderFileServerPort) {
		this.liderFileServerPort = liderFileServerPort;
	}

	public String getLiderFileServerUsername() {
		return liderFileServerUsername;
	}

	public void setLiderFileServerUsername(String liderFileServerUsername) {
		this.liderFileServerUsername = liderFileServerUsername;
	}

	public String getLiderFileServerPwd() {
		return liderFileServerPwd;
	}

	public void setLiderFileServerPwd(String liderFileServerPwd) {
		this.liderFileServerPwd = liderFileServerPwd;
	}

	public String getLiderFileServerPluginPath() {
		return liderFileServerPluginPath;
	}

	public void setLiderFileServerPluginPath(String liderFileServerPluginPath) {
		this.liderFileServerPluginPath = liderFileServerPluginPath;
	}

	public String getLiderFileServerAgreementPath() {
		return liderFileServerAgreementPath;
	}

	public void setLiderFileServerAgreementPath(String liderFileServerAgreementPath) {
		this.liderFileServerAgreementPath = liderFileServerAgreementPath;
	}

	public String getLiderFileServerAgentFilePath() {
		return liderFileServerAgentFilePath;
	}

	public void setLiderFileServerAgentFilePath(String liderFileServerAgentFilePath) {
		this.liderFileServerAgentFilePath = liderFileServerAgentFilePath;
	}

	public String getLiderLdapUseSsl() {
		return liderLdapUseSsl;
	}

	public void setLiderLdapUseSsl(String liderLdapUseSsl) {
		this.liderLdapUseSsl = liderLdapUseSsl;
	}

	public String getLiderXmppUseSsl() {
		return liderXmppUseSsl;
	}

	public void setLiderXmppUseSsl(String liderXmppUseSsl) {
		this.liderXmppUseSsl = liderXmppUseSsl;
	}

}
