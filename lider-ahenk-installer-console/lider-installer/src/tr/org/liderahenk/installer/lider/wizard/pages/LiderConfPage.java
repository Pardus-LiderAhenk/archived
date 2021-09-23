package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LiderConfPage extends WizardPage implements ILiderPage, ControlNextEvent {

	private LiderSetupConfig config;

	private StyledText stMainConfig;
	private StyledText stDatasourceConfig;

	private NextPageEventType nextPageEventType;
	
	// LDAP configuration
	private Text ldapServer;
	private Text ldapPort;
	private Text ldapUsername;
	private Text ldapPassword;
	private Text ldapRootDn;
	private Combo cmbLdapSsl;

	// XMPP configuration
	private Text xmppHost;
	private Text xmppPort;
	private Text xmppUsername;
	private Text xmppPassword;
	private Text xmppResource;
	private Text xmppServiceName;
	private Text xmppMaxRetryConnCount;
	private Text xmppPacketReplyTimeout;
	private Text xmppPingTimeout;
	private Combo cmbXmppSsl;

	// Database configuration
	private Text dbServer;
	private Text dbDatabase;
	private Text dbUsername;
	private Text dbPassword;

	// Agent configuration
	private Text agentLdapBaseDn;
	private Text agentLdapIdAttribute;
	private Text agentLdapJidAttribute;
	private Text agentLdapObjectClasses;

	// User configuration
	private Text userLdapBaseDn;
	private Text userLdapUidAttribute;
	private Text userLdapPrivilegeAttribute;
	private Text userLdapObjectClasses;
	private Text groupLdapObjectClasses;
	
//	private Combo cmbCheckFutureTask;
//	private Combo cmbAlarmCheckReport;
	
	private Text txtFileServerProtocol;
	private Text txtFileServerHost;
	private Text txtFileServerPort;
	private Text txtFileServerUsername;
	private Text txtFileServerPwd;
	private Text txtFileServerPluginPath;
	private Text txtFileServerAgreementPath;
	private Text txtFileServerAgentFilePath;


	public LiderConfPage(LiderSetupConfig config) {
		super(LiderConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("LIDER_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		Label label = GUIHelper.createLabel(container,
				"Hazır gelen değerler diğer bileşenlerin kurulumlarına veya\nvarsayılan değerlere göre getirilmiştir.\nLütfen kontrol ediniz.");
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		container = new ScrolledComposite(container, SWT.V_SCROLL);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Composite innerContainer = new Composite(container, SWT.NONE);
		innerContainer.setLayout(new GridLayout(1, false));
		innerContainer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Composite lineCont = GUIHelper.createComposite(innerContainer, 2);

		//
		// LDAP configuration
		//

		GUIHelper.createLabel(lineCont, "LDAP sunucu adresi");
		ldapServer = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "LDAP sunucu portu");
		ldapPort = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "LDAP Admin kullanıcısı");
		ldapUsername = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "LDAP Admin parolası");
		ldapPassword = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "LDAP kök DN");
		ldapRootDn = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("LDAP_USE_SSL"));
		cmbLdapSsl = new Combo(lineCont, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbLdapSsl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		cmbLdapSsl.setItems(new String[] {Messages.getString("FALSE"), Messages.getString("TRUE")});
		cmbLdapSsl.select(0);
		
		//
		// XMPP configuration
		//
		GUIHelper.createLabel(lineCont, "XMPP sunucu adresi");
		xmppHost = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP sunucu portu");
		xmppPort = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP Lider kullanıcısı");
		xmppUsername = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP kullanıcı parolası");
		xmppPassword = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP Resource Adı");
		xmppResource = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP servis adı");
		xmppServiceName = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP maksimum bağlantı deneme sayısı");
		xmppMaxRetryConnCount = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP paket zamanaşımı süresi");
		xmppPacketReplyTimeout = GUIHelper.createText(lineCont,
				new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "XMPP ping zamanaşımı süresi");
		xmppPingTimeout = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("XMPP_USE_SSL"));
		cmbXmppSsl = new Combo(lineCont, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbXmppSsl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		cmbXmppSsl.setItems(new String[] {Messages.getString("FALSE"), Messages.getString("TRUE")});
		cmbXmppSsl.select(0);
		
		//
		// Database configuration
		//

		GUIHelper.createLabel(lineCont, "Veritabanı sucunu adresi");
		dbServer = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Veritabanı adı");
		dbDatabase = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Veritabanı kullanıcı adı");
		dbUsername = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Veritabanı kullanıcı parolası");
		dbPassword = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		//
		// Agent configuration
		//

		GUIHelper.createLabel(lineCont, "Ajan LDAP kök DN");
		agentLdapBaseDn = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Ajan LDAP ID özniteliği");
		agentLdapIdAttribute = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Ajan LDAP JID özniteliği");
		agentLdapJidAttribute = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Ajan LDAP sınıfları");
		agentLdapObjectClasses = GUIHelper.createText(lineCont,
				new GridData(GridData.FILL, GridData.FILL, true, false));

		//
		// User configuration
		//

		GUIHelper.createLabel(lineCont, "Kullanıcı LDAP kök DN");
		userLdapBaseDn = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Kullanıcı LDAP ID özniteliği");
		userLdapUidAttribute = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Kullanıcı LDAP yetki özniteliği");
		userLdapPrivilegeAttribute = GUIHelper.createText(lineCont,
				new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Kullanıcı LDAP sınıfları");
		userLdapObjectClasses = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, "Kullanıcı grubu LDAP sınıfları");
		groupLdapObjectClasses = GUIHelper.createText(lineCont,
				new GridData(GridData.FILL, GridData.FILL, true, false));
		
		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_PROTOCOL"));
		txtFileServerProtocol = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_HOST"));
		txtFileServerHost = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_PORT"));
		txtFileServerPort = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_USERNAME"));
		txtFileServerUsername = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_PASSWORD"));
		txtFileServerPwd = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_PLUGIN_PATH"));
		txtFileServerPluginPath = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));

		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_AGREEMENT_PATH"));
		txtFileServerAgreementPath = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		
		GUIHelper.createLabel(lineCont, Messages.getString("FILE_SERVER_AGENT_FILE_PATH"));
		txtFileServerAgentFilePath = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		
		GUIHelper.createLabel(innerContainer, "Lider Genel Ayarlar");

		// Add a text area for configuration.
		stMainConfig = new StyledText(innerContainer, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 200;
		stMainConfig.setLayoutData(layoutData);
		stMainConfig.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// If config content is entered user can click next.
				if (!"".equals(stMainConfig.getText()) && stMainConfig.getText() != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

		// Read from file and bring default configuration
		// in the opening of page
		readFile("tr.org.liderahenk.cfg", stMainConfig);

		GUIHelper.createLabel(innerContainer, "Lider Veritabanı Ayarları");

		// Add a text area for configuration.
		stDatasourceConfig = new StyledText(innerContainer, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 100;
		stDatasourceConfig.setLayoutData(layoutData);
		stDatasourceConfig.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// If config content is entered user can click next.
				if (!"".equals(stDatasourceConfig.getText()) && stDatasourceConfig.getText() != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

		// Read from file and bring default configuration
		// in the opening of page
		readFile("tr.org.liderahenk.datasource.cfg", stDatasourceConfig);

		((ScrolledComposite) container).setContent(innerContainer);
		innerContainer.setSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) container).setExpandVertical(true);
		((ScrolledComposite) container).setExpandHorizontal(true);
		((ScrolledComposite) container).setMinSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setPageComplete(false);
	}

	@Override
	public IWizardPage getNextPage() {

		if (nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			// Set default or predefined values to inputs
			setInputValues();
			nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;
		}
		
		// Set config variables before going to next page
		String text = stMainConfig.getText();
		Map<String, String> map = new HashMap<>();
		// LDAP configuration
		map.put("#LDAPSERVER", ldapServer.getText());
		map.put("#LDAPPORT", ldapPort.getText());
		map.put("#LDAPUSERNAME", ldapUsername.getText());
		map.put("#LDAPPASSWORD", ldapPassword.getText());
		map.put("#LDAPROOTDN", ldapRootDn.getText());
		map.put("#LDAP_SSL", cmbLdapSsl.getText());
		// XMPP configuration
		map.put("#XMPPHOST", xmppHost.getText());
		map.put("#XMPPPORT", xmppPort.getText());
		map.put("#XMPPUSERNAME", xmppUsername.getText());
		map.put("#XMPPPASSWORD", xmppPassword.getText());
		map.put("#XMPPRESOURCE", xmppResource.getText());
		map.put("#XMPPSERVICENAME", xmppServiceName.getText());
		map.put("#XMPPMAXRETRY", xmppMaxRetryConnCount.getText());
		map.put("#XMPPREPLAYTIMEOUT", xmppPacketReplyTimeout.getText());
		map.put("#XMPPPINGTIMEOUT", xmppPingTimeout.getText());
		map.put("#XMPP_SSL", cmbXmppSsl.getText());
		map.put("#XMPP_PRESENCE_PRIORITY", "1");
		// Agent configuration
		map.put("#AGENTLDAPBASEDN", agentLdapBaseDn.getText());
		map.put("#AGENTLDAPIDATTR", agentLdapIdAttribute.getText());
		map.put("#AGENTLDAPJIDATTR", agentLdapJidAttribute.getText());
		map.put("#AGENTLDAPOBJECTCLASSES", agentLdapObjectClasses.getText());
		// User configuration
		map.put("#USERLDAPBASEDN", userLdapBaseDn.getText());
		map.put("#USERLDAPUIDATTR", userLdapUidAttribute.getText());
		map.put("#USERLDAPPRIVILEGEATTR", userLdapPrivilegeAttribute.getText());
		map.put("#USERLDAPOBJECTCLASSES", userLdapObjectClasses.getText());
		map.put("#GROUPLDAPOBJECTCLASSES", groupLdapObjectClasses.getText());

		map.put("#CHECK_FUTURE_TASK", "true");
		map.put("#ALARM_CHECK_REPORT", "true");

		map.put("#FILE_SERVER_PROTOCOL", txtFileServerProtocol.getText());
		map.put("#FILE_SERVER_HOST", txtFileServerHost.getText());
		map.put("#FILE_SERVER_PORT", txtFileServerPort.getText());
		map.put("#FILE_SERVER_USERNAME", txtFileServerUsername.getText());
		map.put("#FILE_SERVER_PWD", txtFileServerPwd.getText());
		map.put("#FILE_SERVER_PLUGIN_PATH", txtFileServerPluginPath.getText());
		map.put("#FILE_SERVER_AGREEMENT_PATH", txtFileServerAgreementPath.getText());
		map.put("#FILE_SERVER_AGENT_FILE_PATH", txtFileServerAgentFilePath.getText());
		
		text = LiderAhenkUtils.replace(map, text);
		config.setLiderConfContent(text);
		config.setLiderAbsPathConfFile(LiderAhenkUtils.writeToFileReturnPath(text, "tr.org.liderahenk.cfg"));

		// Database configuration
		String text2 = stDatasourceConfig.getText();
		Map<String, String> map2 = new HashMap<>();
		map2.put("#DBADDRESS", dbServer.getText());
		map2.put("#DBDATABASE", dbDatabase.getText());
		map2.put("#DBUSERNAME", dbUsername.getText());
		map2.put("#DBPASSWORD", dbPassword.getText());

		text2 = LiderAhenkUtils.replace(map2, text2);
		config.setDatasourceConfContent(text2);
		config.setDatasourceAbsPathConfFile(LiderAhenkUtils.writeToFileReturnPath(text2, "tr.org.liderahenk.datasource.cfg"));

		return super.getNextPage();
	}

	private void setInputValues() {
		ldapServer.setText(config.getLdapIp() != null ? config.getLdapIp() : "ldap." + config.getLdapOrgCn());
		ldapPort.setText("389");
		if (config.isLdapUpdate()) { 
			ldapUsername.setText(config.getLdapAdminDn());
		} else {
			ldapUsername.setText(
					config.getLdapAdminCn() != null ? "cn=" + config.getLdapAdminCn() + "," + config.getLdapBaseDn()
					: "cn=admin," + config.getLdapBaseDn());
		}
		ldapPassword.setText(config.getLdapAdminCnPwd() != null ? config.getLdapAdminCnPwd() : "secret");
		ldapRootDn.setText(config.getLdapBaseDn());
		xmppHost.setText(config.getXmppIp() != null ? config.getXmppIp() : "im." + config.getLdapOrgCn());
		xmppPort.setText("5222");
		xmppUsername.setText(config.getXmppLiderUsername() != null ? config.getXmppLiderUsername() : "lider_sunucu");
		xmppPassword.setText(config.getXmppLiderPassword() != null ? config.getXmppLiderPassword() : "asddsa123");
		xmppResource.setText("Smack");
		xmppServiceName.setText(config.getXmppHostname() != null ? config.getXmppHostname() : "im." + config.getLdapOrgCn());
		xmppMaxRetryConnCount.setText("5");
		xmppPacketReplyTimeout.setText("10000");
		xmppPingTimeout.setText("3000");
		if (!config.isDatabaseCluster()) {
			dbServer.setText(config.getDatabaseIp() != null ? config.getDatabaseIp() + ":3306" : "db." + config.getLdapOrgCn() + ":3306");
		} else {
			dbServer.setText(config.getDatabaseClusterAddressForLider() != null ? config.getDatabaseClusterAddressForLider() : "db." + config.getLdapOrgCn() + ":3306");
		}
		dbDatabase.setText("liderdb");
		dbUsername.setText("root");
		dbPassword.setText(config.getDatabaseRootPassword() != null && !config.getDatabaseRootPassword().isEmpty()
				? config.getDatabaseRootPassword() : "qwert123");
		agentLdapBaseDn.setText("ou=Ahenkler," + config.getLdapBaseDn());
		agentLdapIdAttribute.setText("cn");
		agentLdapJidAttribute.setText("uid");
		agentLdapObjectClasses.setText("pardusDevice,device");
		userLdapBaseDn.setText(config.getLdapBaseDn());
		userLdapUidAttribute.setText("uid");
		userLdapPrivilegeAttribute.setText("liderPrivilege");
		userLdapObjectClasses.setText("pardusAccount,pardusLider");
		groupLdapObjectClasses.setText("groupOfNames");
		
		txtFileServerProtocol.setText("ssh");
		txtFileServerHost.setText("liderahenk.org");
		txtFileServerPort.setText("22");
		txtFileServerUsername.setText("distro");
		txtFileServerPwd.setText("!Distr0");
		txtFileServerPluginPath.setText("/plugins/ahenk-{0}_{1}_amd64.deb");
		txtFileServerAgreementPath.setText("/sample-agreement.txt");
		txtFileServerAgentFilePath.setText("/agent-files/{0}/");
	}

	/**
	 * Reads file from classpath location for current project and sets it to a
	 * text in a GUI.
	 * 
	 * @param fileName
	 */
	private void readFile(String fileName, final StyledText guiText) {

		BufferedReader br = null;
		InputStream inputStream = null;

		try {
			String currentLine;

			inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

			br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			String readingText = "";

			while ((currentLine = br.readLine()) != null) {
				// Platform independent line separator.
				readingText += currentLine + System.getProperty("line.separator");
			}

			final String tmpText = readingText;
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					guiText.setText(tmpText);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return this.nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
