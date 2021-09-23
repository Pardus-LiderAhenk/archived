package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.model.LiderNodeInfoModel;
import tr.org.liderahenk.installer.lider.wizard.model.LiderNodeSwtModel;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class LiderClusterConfPage extends WizardPage implements ILiderPage {

	private LiderSetupConfig config;

	private Composite cmpMain;

	private Button btnAddRemoveNode;

	private Text txtLdapAddress;
	private Text txtLdapPort;
	private Text txtLdapAdminUser;
	private Text txtLdapAdminPwd;
	private Text txtLdapBaseDn;
	private Combo cmbLdapSsl;

	private Text txtXmppAddress;
	private Text txtXmppPort;
	private Text txtXmppLiderUser;
	private Text txtXmppLiderPwd;
	private Text txtXmppServiceName;
	private Text txtXmppMaxTrials;
	private Text txtXmppPacketTimeout;
	private Text txtXmppPingTimeout;
	private Combo cmbXmppSsl;

	private Text txtDatabaseAddress;
	private Text txtDatabaseName;
	private Text txtDatabaseUsername;
	private Text txtDatabasePwd;

	private Text txtAgentLdapBaseDn;
	private Text txtAgentLdapIdAttribute;
	private Text txtAgentLdapJidAttribute;
	private Text txtAgentLdapClasses;

	private Text txtUserLdapBaseDn;
	private Text txtUserLdapIdAttribute;
	private Text txtUserLdapPrivilegeAttribute;
	private Text txtUserLdapClasses;
	private Text txtUserGroupLdapClasses;

	private Text txtFileServerProtocol;
	private Text txtFileServerHost;
	private Text txtFileServerPort;
	private Text txtFileServerUsername;
	private Text txtFileServerPwd;
	private Text txtFileServerPluginPath;
	private Text txtFileServerAgreementPath;
	private Text txtFileServerAgentFilePath;

	private Button btnUsePrivateKey;
	private Text txtPrivateKey;
	private Button btnUploadKey;
	private FileDialog dialog;
	private String selectedFile;
	private Text txtPassphrase;
	private Text txtProxyAddress;
	private Text txtProxyPwd;

	private Map<Integer, LiderNodeSwtModel> nodeMap = new HashMap<Integer, LiderNodeSwtModel>();

	private Composite innerContainer;

	private NextPageEventType nextPageEventType = NextPageEventType.CLICK_FROM_PREV_PAGE;

	public LiderClusterConfPage(LiderSetupConfig config) {
		super(LiderClusterConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription(Messages.getString("LIDER_CLUSTER_CONF", "3.4"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		cmpMain = GUIHelper.createComposite(parent, 1);
		setControl(cmpMain);

		GUIHelper.createLabel(cmpMain, "");

		cmpMain = new ScrolledComposite(cmpMain, SWT.V_SCROLL);
		cmpMain.setLayout(new GridLayout(1, false));
		cmpMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		innerContainer = new Composite(cmpMain, SWT.NONE);
		innerContainer.setLayout(new GridLayout(1, false));
		innerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		Label lblGeneralInfo = GUIHelper.createLabel(innerContainer, Messages.getString("LIDER_CLUSTER_GENERAL_INFO"));
		lblGeneralInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		Composite cmpGeneralInfo = GUIHelper.createComposite(innerContainer, 2);
		cmpGeneralInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// General parameters' inputs
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_SERVER_ADDRESS"));
		txtLdapAddress = GUIHelper.createText(cmpGeneralInfo);
		txtLdapAddress.setMessage(Messages.getString("ENTER_IP_OF_LDAP_SERVER"));
		txtLdapAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_PORT"));
		txtLdapPort = GUIHelper.createText(cmpGeneralInfo);
		txtLdapPort.setMessage(Messages.getString("ENTER_PORT_OF_LDAP"));
		txtLdapPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_ADMIN_USER"));
		txtLdapAdminUser = GUIHelper.createText(cmpGeneralInfo);
		txtLdapAdminUser.setMessage(Messages.getString("ENTER_ADMIN_USER_OF_LDAP"));
		txtLdapAdminUser.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_ADMIN_USER_PWD"));
		txtLdapAdminPwd = GUIHelper.createText(cmpGeneralInfo);
		txtLdapAdminPwd.setMessage(Messages.getString("ENTER_PWD_OF_ADMIN_USER_OF_LDAP"));
		txtLdapAdminPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_BASE_DN"));
		txtLdapBaseDn = GUIHelper.createText(cmpGeneralInfo);
		txtLdapBaseDn.setMessage(Messages.getString("ENTER_BASE_DN_OF_LDAP"));
		txtLdapBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_USE_SSL"));
		cmbLdapSsl = new Combo(cmpGeneralInfo, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbLdapSsl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		cmbLdapSsl.setItems(new String[] {"false", "true"});
		cmbLdapSsl.select(0);

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_SERVER_ADDRESS"));
		txtXmppAddress = GUIHelper.createText(cmpGeneralInfo);
		txtXmppAddress.setMessage(Messages.getString("ENTER_IP_OF_XMPP"));
		txtXmppAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_PORT"));
		txtXmppPort = GUIHelper.createText(cmpGeneralInfo);
		txtXmppPort.setMessage(Messages.getString("ENTER_PORT_OF_XMPP_SERVER"));
		txtXmppPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_LIDER_USER"));
		txtXmppLiderUser = GUIHelper.createText(cmpGeneralInfo);
		txtXmppLiderUser.setMessage(Messages.getString("ENTER_USERNAME_OF_XMPP_LIDER_USER"));
		txtXmppLiderUser.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_LIDER_USER_PWD"));
		txtXmppLiderPwd = GUIHelper.createText(cmpGeneralInfo);
		txtXmppLiderPwd.setMessage(Messages.getString("ENTER_PWD_OF_XMPP_LIDER_USER"));
		txtXmppLiderPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_SERVICE_NAME"));
		txtXmppServiceName = GUIHelper.createText(cmpGeneralInfo);
		txtXmppServiceName.setMessage(Messages.getString("ENTER_SERVICE_NAME_OF_XMPP"));
		txtXmppServiceName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_MAX_CONNECTION_TRIALS"));
		txtXmppMaxTrials = GUIHelper.createText(cmpGeneralInfo);
		txtXmppMaxTrials.setMessage(Messages.getString("ENTER_MAX_NUMBER_FOR_TRIALS"));
		txtXmppMaxTrials.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_PACKET_TIMEOUT"));
		txtXmppPacketTimeout = GUIHelper.createText(cmpGeneralInfo);
		txtXmppPacketTimeout.setMessage(Messages.getString("ENTER_PACKET_TIMEOUT"));
		txtXmppPacketTimeout.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_PING_TIMEOUT"));
		txtXmppPingTimeout = GUIHelper.createText(cmpGeneralInfo);
		txtXmppPingTimeout.setMessage(Messages.getString("ENTER_PING_TIMEOUT"));
		txtXmppPingTimeout.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_USE_SSL"));
		cmbXmppSsl = new Combo(cmpGeneralInfo, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbXmppSsl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		cmbXmppSsl.setItems(new String[] {"false", "true"});
		cmbXmppSsl.select(0);

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("DATABASE_SERVER_ADDRESS"));
		txtDatabaseAddress = GUIHelper.createText(cmpGeneralInfo);
		txtDatabaseAddress.setMessage(Messages.getString("ENTER_IP_OF_DATABASE_SERVER"));
		txtDatabaseAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("DATABASE_USERNAME"));
		txtDatabaseUsername = GUIHelper.createText(cmpGeneralInfo);
		txtDatabaseUsername.setMessage(Messages.getString("ENTER_USERNAME_OF_DATABASE"));
		txtDatabaseUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("DATABASE_PWD"));
		txtDatabasePwd = GUIHelper.createText(cmpGeneralInfo);
		txtDatabasePwd.setMessage(Messages.getString("ENTER_PWD_OF_USERNAME_OF_DATABASE"));
		txtDatabasePwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("DATABASE_NAME"));
		txtDatabaseName = GUIHelper.createText(cmpGeneralInfo);
		txtDatabaseName.setMessage(Messages.getString("ENTER_NAME_OF_DATABASE"));
		txtDatabaseName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("AGENT_LDAP_BASE_DN"));
		txtAgentLdapBaseDn = GUIHelper.createText(cmpGeneralInfo);
		txtAgentLdapBaseDn.setMessage(Messages.getString("ENTER_NAME_OF_DATABASE"));
		txtAgentLdapBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("AGENT_LDAP_ID_ATTRIBUTE"));
		txtAgentLdapIdAttribute = GUIHelper.createText(cmpGeneralInfo);
		txtAgentLdapIdAttribute.setMessage(Messages.getString("ENTER_AGENT_LDAP_ID_ATTRIBUTE"));
		txtAgentLdapIdAttribute.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("AGENT_LDAP_JID_ATTRIBUTE"));
		txtAgentLdapJidAttribute = GUIHelper.createText(cmpGeneralInfo);
		txtAgentLdapJidAttribute.setMessage(Messages.getString("ENTER_AGENT_LDAP_JID_ATTRIBUTE"));
		txtAgentLdapJidAttribute.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("AGENT_LDAP_CLASSES"));
		txtAgentLdapClasses = GUIHelper.createText(cmpGeneralInfo);
		txtAgentLdapClasses.setMessage(Messages.getString("ENTER_AGENT_LDAP_CLASSES"));
		txtAgentLdapClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("USER_LDAP_BASE_DN"));
		txtUserLdapBaseDn = GUIHelper.createText(cmpGeneralInfo);
		txtUserLdapBaseDn.setMessage(Messages.getString("ENTER_USER_LDAP_BASE_DN"));
		txtUserLdapBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("USER_LDAP_ID_ATTRIBUTE"));
		txtUserLdapIdAttribute = GUIHelper.createText(cmpGeneralInfo);
		txtUserLdapIdAttribute.setMessage(Messages.getString("ENTER_USER_LDAP_ID_ATTRIBUTE"));
		txtUserLdapIdAttribute.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("USER_LDAP_PRIVILEGE_ATTRIBUTE"));
		txtUserLdapPrivilegeAttribute = GUIHelper.createText(cmpGeneralInfo);
		txtUserLdapPrivilegeAttribute.setMessage(Messages.getString("ENTER_USER_LDAP_PRIVILEGE_ATTRIBUTE"));
		txtUserLdapPrivilegeAttribute.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("USER_LDAP_ID_ATTRIBUTE"));
		txtUserLdapClasses = GUIHelper.createText(cmpGeneralInfo);
		txtUserLdapClasses.setMessage(Messages.getString("ENTER_USER_LDAP_ID_ATTRIBUTE"));
		txtUserLdapClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("USER_GROUP_LDAP_CLASSES"));
		txtUserGroupLdapClasses = GUIHelper.createText(cmpGeneralInfo);
		txtUserGroupLdapClasses.setMessage(Messages.getString("ENTER_USER_GROUP_LDAP_CLASSES"));
		txtUserGroupLdapClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_PROTOCOL"));
		txtFileServerProtocol = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerProtocol.setMessage(Messages.getString("ENTER_PROTOCOL_FOR_FILE_SERVER"));
		txtFileServerProtocol.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_HOST"));
		txtFileServerHost = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerHost.setMessage(Messages.getString("ENTER_FILE_SERVER_HOST"));
		txtFileServerHost.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_PORT"));
		txtFileServerPort = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerPort.setMessage(Messages.getString("ENTER_FILE_SERVER_PORT"));
		txtFileServerPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_USERNAME"));
		txtFileServerUsername = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerUsername.setMessage(Messages.getString("ENTER_FILE_SERVER_USERNAME"));
		txtFileServerUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_PASSWORD"));
		txtFileServerPwd = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerPwd.setMessage(Messages.getString("ENTER_FILE_SERVER_PASSWORD"));
		txtFileServerPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_PLUGIN_PATH"));
		txtFileServerPluginPath = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerPluginPath.setMessage(Messages.getString("ENTER_FILE_SERVER_PLUGIN_PATH"));
		txtFileServerPluginPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_AGREEMENT_PATH"));
		txtFileServerAgreementPath = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerAgreementPath.setMessage(Messages.getString("ENTER_FILE_SERVER_AGREEMENT_PATH"));
		txtFileServerAgreementPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("FILE_SERVER_AGENT_FILE_PATH"));
		txtFileServerAgentFilePath = GUIHelper.createText(cmpGeneralInfo);
		txtFileServerAgentFilePath.setMessage(Messages.getString("ENTER_FILE_SERVER_AGENT_FILE_PATH"));
		txtFileServerAgentFilePath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("HAPROXY_ADDRESS"));
		txtProxyAddress = GUIHelper.createText(cmpGeneralInfo);
		txtProxyAddress.setMessage(Messages.getString("ENTER_IP_ADDRESS_OF_HAPROXY"));
		txtProxyAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("HAPROXY_PWD"));
		txtProxyPwd = GUIHelper.createText(cmpGeneralInfo);
		txtProxyPwd.setMessage(Messages.getString("ENTER_PWD_OF_HAPROXY_MACHINE"));
		txtProxyPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		Composite cmpPrivateKey = GUIHelper.createComposite(innerContainer, 3);
		cmpPrivateKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// Create a dialog window.
		dialog = new FileDialog(innerContainer.getShell(), SWT.SAVE);
		dialog.setText(Messages.getString("UPLOAD_KEY"));

		btnUsePrivateKey = GUIHelper.createButton(cmpPrivateKey, SWT.CHECK | SWT.BORDER,
				Messages.getString("USE_PRIVATE_KEY"));
		btnUsePrivateKey.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				organizePasswordFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		txtPrivateKey = GUIHelper.createText(cmpPrivateKey);
		txtPrivateKey.setEnabled(false);
		// User should not be able to write
		// anything to this text field.
		txtPrivateKey.setEditable(false);

		btnUploadKey = GUIHelper.createButton(cmpPrivateKey, SWT.PUSH | SWT.BORDER,
				Messages.getString("UPLOAD_PRIVATE_KEY"));
		btnUploadKey.setEnabled(false);
		btnUploadKey.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openDialog();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		GUIHelper.createLabel(cmpPrivateKey, Messages.getString("PASSPHRASE"));
		txtPassphrase = GUIHelper.createText(cmpPrivateKey);
		txtPassphrase.setEnabled(false);

		Label lblNodeInfo = GUIHelper.createLabel(innerContainer, Messages.getString("KARAF_CELLAR_NODE_INFO"));
		lblNodeInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		Composite cmpNodeList = GUIHelper.createComposite(innerContainer, 2);
		cmpNodeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// Add at least 2 nodes
		createNewNode(cmpNodeList, true);
		GUIHelper.createLabel(cmpNodeList);
		createNewNode(cmpNodeList, false);

		btnAddRemoveNode = GUIHelper.createButton(cmpNodeList, SWT.PUSH);
		btnAddRemoveNode
				.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/add.png")));
		btnAddRemoveNode.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleAddButtonClick(event);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		setPageComplete(false);

		((ScrolledComposite) cmpMain).setContent(innerContainer);
		innerContainer.setSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) cmpMain).setExpandVertical(true);
		((ScrolledComposite) cmpMain).setExpandHorizontal(true);
		((ScrolledComposite) cmpMain).setMinSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void updatePageCompleteStatus() {

		boolean pageComplete = false;

		if (!txtLdapAddress.getText().isEmpty() && !txtXmppPort.getText().isEmpty() && !txtLdapPort.getText().isEmpty()
				&& !txtLdapAdminUser.getText().isEmpty() && !txtLdapAdminPwd.getText().isEmpty()
				&& !txtLdapBaseDn.getText().isEmpty() && !txtXmppAddress.getText().isEmpty()
				&& !txtXmppPort.getText().isEmpty() && !txtXmppLiderUser.getText().isEmpty()
				&& !txtXmppLiderPwd.getText().isEmpty() && !txtXmppServiceName.getText().isEmpty()
				&& !txtXmppMaxTrials.getText().isEmpty() && !txtXmppPacketTimeout.getText().isEmpty()
				&& !txtXmppPingTimeout.getText().isEmpty() && !txtDatabaseAddress.getText().isEmpty()
				&& !txtDatabaseName.getText().isEmpty() && !txtDatabaseUsername.getText().isEmpty()
				&& !txtDatabasePwd.getText().isEmpty() && !txtAgentLdapBaseDn.getText().isEmpty()
				&& !txtAgentLdapIdAttribute.getText().isEmpty() && !txtAgentLdapJidAttribute.getText().isEmpty()
				&& !txtAgentLdapClasses.getText().isEmpty() && !txtUserLdapBaseDn.getText().isEmpty()
				&& !txtUserLdapIdAttribute.getText().isEmpty() && !txtUserLdapPrivilegeAttribute.getText().isEmpty()
				&& !txtUserLdapClasses.getText().isEmpty() && !txtUserGroupLdapClasses.getText().isEmpty()
				&& !txtFileServerProtocol.getText().isEmpty() && !txtFileServerHost.getText().isEmpty()
				&& !txtFileServerPort.getText().isEmpty() && !txtFileServerUsername.getText().isEmpty()
				&& !txtFileServerPwd.getText().isEmpty() && !txtFileServerPluginPath.getText().isEmpty()
				&& !txtFileServerAgreementPath.getText().isEmpty() && !txtFileServerAgentFilePath.getText().isEmpty()
				&& (btnUsePrivateKey.getSelection() ? true : !txtProxyPwd.getText().isEmpty())) {

			for (Iterator<Entry<Integer, LiderNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
					.hasNext();) {
				Entry<Integer, LiderNodeSwtModel> entry = iterator.next();
				LiderNodeSwtModel node = entry.getValue();

				if (!node.getTxtNodeIp().getText().isEmpty()) {
					if (btnUsePrivateKey.getSelection()) {
						if (!txtPrivateKey.getText().isEmpty()) {
							pageComplete = true;
						} else {
							pageComplete = false;
							break;
						}
					} else {
						if (!node.getTxtNodeRootPwd().getText().isEmpty()) {
							pageComplete = true;
						} else {
							pageComplete = false;
							break;
						}
					}
				} else {
					pageComplete = false;
					break;
				}
			}

			setPageComplete(pageComplete);
		} else {
			setPageComplete(false);
		}
	}

	private void organizePasswordFields() {
		if (btnUsePrivateKey.getSelection()) {
			txtPrivateKey.setEnabled(true);
			btnUploadKey.setEnabled(true);
			txtPassphrase.setEnabled(true);
			txtProxyPwd.setEnabled(false);
		} else {
			txtPrivateKey.setEnabled(false);
			btnUploadKey.setEnabled(false);
			txtPassphrase.setEnabled(false);
			txtProxyPwd.setEnabled(true);
		}

		for (Iterator<Entry<Integer, LiderNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<Integer, LiderNodeSwtModel> entry = iterator.next();
			LiderNodeSwtModel node = entry.getValue();
			if (btnUsePrivateKey.getSelection()) {
				node.getTxtNodeRootPwd().setEnabled(false);
			} else {
				node.getTxtNodeRootPwd().setEnabled(true);
			}
		}
	}

	/**
	 * This method opens a dialog when triggered, and sets the private key text
	 * field.
	 */
	private void openDialog() {
		selectedFile = dialog.open();
		if (selectedFile != null && !"".equals(selectedFile)) {
			txtPrivateKey.setText(selectedFile);
		}
	}

	private void createNewNode(Composite cmpNodeList, boolean addLabels) {
		Group grpClusterNode = GUIHelper.createGroup(cmpNodeList, 6);
		grpClusterNode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		GridData gd = new GridData();
		gd.widthHint = 190;

		if (addLabels) {
			GUIHelper.createLabel(grpClusterNode, "");
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_IP"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_USERNAME"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_PWD"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_XMPP_RESOURCE"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_XMPP_PRIORITY"));
		}
		
		LiderNodeSwtModel clusterNode = new LiderNodeSwtModel();

		Integer nodeNumber = nodeMap.size() + 1;
		clusterNode.setNodeNumber(nodeNumber);

		GUIHelper.createLabel(grpClusterNode, nodeNumber.toString());

		Text txtNodeIp = GUIHelper.createText(grpClusterNode);
		txtNodeIp.setLayoutData(gd);
		txtNodeIp.setMessage(Messages.getString("ENTER_IP_FOR_THIS_NODE"));
		txtNodeIp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeIp(txtNodeIp);

		Text txtNodeUsername = GUIHelper.createText(grpClusterNode);
		txtNodeUsername.setLayoutData(gd);
		txtNodeUsername.setMessage(Messages.getString("ENTER_USERNAME_OF_THIS_NODE"));
		txtNodeUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeUsername(txtNodeUsername);
		txtNodeUsername.setText("root");
		
		Text txtNodeRootPwd = GUIHelper.createText(grpClusterNode);
		txtNodeRootPwd.setLayoutData(gd);
		txtNodeRootPwd.setMessage(Messages.getString("ENTER_PWD_OF_THIS_NODE"));
		txtNodeRootPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeRootPwd(txtNodeRootPwd);

		Text txtXmppResource = GUIHelper.createText(grpClusterNode);
		txtXmppResource.setLayoutData(gd);
		txtXmppResource.setMessage(Messages.getString("ENTER_RESOURCE_NAME_FOR_XMPP"));
		txtXmppResource.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeXmppResource(txtXmppResource);

		Text txtXmppPresencePriority = GUIHelper.createText(grpClusterNode);
		txtXmppPresencePriority.setLayoutData(gd);
		txtXmppPresencePriority.setMessage(Messages.getString("ENTER_PRESENCE_PRIORITY_FOR_XMPP"));
		txtXmppPresencePriority.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeXmppPresencePriority(txtXmppPresencePriority);

		nodeMap.put(nodeNumber, clusterNode);
	}

	private void handleAddButtonClick(SelectionEvent event) {
		Composite parent = (Composite) ((Button) event.getSource()).getParent();
		createNewNode(parent, false);

		Button btnRemoveNode = GUIHelper.createButton(parent, SWT.PUSH);
		btnRemoveNode
				.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/remove.png")));
		btnRemoveNode.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleRemoveButtonClick(event);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		redraw();

		innerContainer.setSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) cmpMain).setMinSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		updatePageCompleteStatus();
	}

	private void handleRemoveButtonClick(SelectionEvent event) {
		Button btnThis = (Button) event.getSource();
		Composite parent = btnThis.getParent();
		Control[] children = parent.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(btnThis) && i - 1 > 0) {
					Group group = (Group) children[i - 1];
					Control[] childrenOfGroup = group.getChildren();
					Label number = (Label) childrenOfGroup[0];
					Integer intNumber = Integer.parseInt(number.getText());
					nodeMap.remove(intNumber);
					children[i - 1].dispose();
					children[i].dispose();
					redraw();
					break;
				}
			}
		}

		innerContainer.setSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) cmpMain).setMinSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		updatePageCompleteStatus();
	}

	private void redraw() {
		cmpMain.redraw();
		cmpMain.layout(true, true);
	}

	@Override
	public IWizardPage getNextPage() {

		setConfigVariables();

		return super.getNextPage();
	}

	private void setConfigVariables() {

		config.setLiderProxyAddress(txtProxyAddress.getText());
		config.setLiderNodeInfoMap(createInfoModelMap());

		config.setLiderLdapServerAddress(txtLdapAddress.getText());
		config.setLiderLdapPort(new Integer(txtLdapPort.getText()));
		config.setLiderLdapAdminUser(txtLdapAdminUser.getText());
		config.setLiderLdapAdminPwd(txtLdapAdminPwd.getText());
		config.setLiderLdapBaseDn(txtLdapBaseDn.getText());
		config.setLiderLdapUseSsl(cmbLdapSsl.getText());

		config.setLiderXmppAddress(txtXmppAddress.getText());
		config.setLiderXmppPort(new Integer(txtXmppPort.getText()));
		config.setLiderXmppLiderUser(txtXmppLiderUser.getText());
		config.setLiderXmppLiderPwd(txtXmppLiderPwd.getText());
		config.setLiderXmppServiceName(txtXmppServiceName.getText());
		config.setLiderXmppMaxTrials(txtXmppMaxTrials.getText());
		config.setLiderXmppPacketTimeout(txtXmppPacketTimeout.getText());
		config.setLiderXmppPingTimeout(txtXmppPingTimeout.getText());
		config.setLiderXmppUseSsl(cmbXmppSsl.getText());

		config.setLiderDbAddress(txtDatabaseAddress.getText());
		config.setLiderDbName(txtDatabaseName.getText());
		config.setLiderDbUsername(txtDatabaseUsername.getText());
		config.setLiderDbPwd(txtDatabasePwd.getText());

		config.setLiderAgentLdapBaseDn(txtAgentLdapBaseDn.getText());
		config.setLiderAgentLdapIdAttribute(txtAgentLdapIdAttribute.getText());
		config.setLiderAgentLdapJidAttribute(txtAgentLdapJidAttribute.getText());
		config.setLiderAgentLdapClasses(txtAgentLdapClasses.getText());

		config.setLiderUserLdapBaseDn(txtUserLdapBaseDn.getText());
		config.setLiderUserLdapIdAttribute(txtUserLdapIdAttribute.getText());
		config.setLiderUserLdapPrivilegeAttribute(txtUserLdapPrivilegeAttribute.getText());
		config.setLiderUserLdapClasses(txtUserLdapClasses.getText());
		config.setLiderUserGroupLdapClasses(txtUserGroupLdapClasses.getText());

		if (btnUsePrivateKey.getSelection()) {
			config.setLiderAccessKeyPath(txtPrivateKey.getText());
			config.setLiderAccessPassphrase(txtPassphrase.getText());
			config.setLiderProxyPwd(null);
		} else {
			config.setLiderAccessKeyPath(null);
			config.setLiderAccessPassphrase(null);
			config.setLiderProxyPwd(txtProxyPwd.getText());
		}

		config.setLiderFileServerProtocol(txtFileServerProtocol.getText());
		config.setLiderFileServerHost(txtFileServerHost.getText());
		config.setLiderFileServerPort(txtFileServerPort.getText());
		config.setLiderFileServerUsername(txtFileServerUsername.getText());
		config.setLiderFileServerPwd(txtFileServerPwd.getText());
		config.setLiderFileServerPluginPath(txtFileServerPluginPath.getText());
		config.setLiderFileServerAgreementPath(txtFileServerAgreementPath.getText());
		config.setLiderFileServerAgentFilePath(txtFileServerAgentFilePath.getText());

	}

	private Map<Integer, LiderNodeInfoModel> createInfoModelMap() {
		Map<Integer, LiderNodeInfoModel> nodeInfoMap = new HashMap<Integer, LiderNodeInfoModel>();

		for (Iterator<Entry<Integer, LiderNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<Integer, LiderNodeSwtModel> entry = iterator.next();
			LiderNodeSwtModel nodeSwt = entry.getValue();

			LiderNodeInfoModel nodeInfo = new LiderNodeInfoModel(nodeSwt.getNodeNumber(),
					nodeSwt.getTxtNodeIp().getText(), nodeSwt.getTxtNodeUsername().getText(),
					btnUsePrivateKey.getSelection() ? null : nodeSwt.getTxtNodeRootPwd().getText(),
					nodeSwt.getTxtNodeXmppResource().getText(), nodeSwt.getTxtNodeXmppPresencePriority().getText());

			nodeInfoMap.put(nodeSwt.getNodeNumber(), nodeInfo);
		}

		return nodeInfoMap;
	}

	private void setInputValues() {

		txtLdapAddress.setText(config.getLdapIp() != null ? config.getLdapIp() : "ldap." + config.getLdapOrgCn());
		txtLdapPort.setText("389");
		txtLdapAdminUser.setText(
				config.getLdapAdminCn() != null ? "cn=" + config.getLdapAdminCn() + "," + config.getLdapBaseDn()
						: "cn=admin," + config.getLdapBaseDn());

		txtLdapAdminPwd.setText(config.getLdapAdminCnPwd() != null ? config.getLdapAdminCnPwd() : "");
		txtLdapBaseDn.setText(config.getLdapBaseDn() != null ? config.getLdapBaseDn() : "dc=mys,dc=pardus,dc=org");

		if (config.isXmppCluster()) {
			txtXmppAddress.setText(config.getXmppProxyAddress());
		} else {
			txtXmppAddress.setText(config.getXmppIp() != null ? config.getXmppIp() : "im." + config.getLdapOrgCn());
		}
		txtXmppPort.setText("5222");
		txtXmppLiderUser
				.setText(config.getXmppLiderUsername() != null ? config.getXmppLiderUsername() : "lider_sunucu");
		txtXmppLiderPwd.setText(config.getXmppLiderPassword() != null ? config.getXmppLiderPassword() : "");
		txtXmppServiceName
				.setText(config.getXmppHostname() != null ? config.getXmppHostname() : "im." + config.getLdapOrgCn());
		txtXmppMaxTrials.setText("5");
		txtXmppPacketTimeout.setText("10000");
		txtXmppPingTimeout.setText("3000");

		if (!config.isDatabaseCluster()) {
			txtDatabaseAddress.setText(config.getDatabaseIp() != null ? config.getDatabaseIp() + ":3306"
					: "db." + config.getLdapOrgCn() + ":3306");
		} else {
			txtDatabaseAddress.setText(config.getDatabaseClusterAddressForLider() != null
					? config.getDatabaseClusterAddressForLider() : "db." + config.getLdapOrgCn() + ":3306");
		}
		txtDatabaseName.setText("liderdb");
		txtDatabaseUsername.setText("root");
		txtDatabasePwd.setText(config.getDatabaseRootPassword() != null ? config.getDatabaseRootPassword() : "");

		txtAgentLdapBaseDn.setText("ou=Ahenkler," + config.getLdapBaseDn());
		txtAgentLdapIdAttribute.setText("cn");
		txtAgentLdapJidAttribute.setText("uid");
		txtAgentLdapClasses.setText("pardusDevice,device");

		txtUserLdapBaseDn.setText(config.getLdapBaseDn());
		txtUserLdapIdAttribute.setText("uid");
		txtUserLdapPrivilegeAttribute.setText("liderPrivilege");
		txtUserLdapClasses.setText("pardusAccount,pardusLider");
		txtUserGroupLdapClasses.setText("groupOfNames");

		txtFileServerProtocol.setText("ssh");
		txtFileServerHost.setText("liderahenk.org");
		txtFileServerPort.setText("22");
		txtFileServerUsername.setText("distro");
		txtFileServerPwd.setText("!Distr0");
		txtFileServerPluginPath.setText("/plugins/ahenk-{0}_{1}_amd64.deb");
		txtFileServerAgreementPath.setText("/sample-agreement.txt");
		txtFileServerAgentFilePath.setText("/agent-files/{0}/");
	}

	@Override
	public IWizardPage getPreviousPage() {
		if (nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;
			setInputValues();
		}

		return super.getPreviousPage();
	}

}
