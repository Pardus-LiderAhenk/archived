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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.model.XmppNodeInfoModel;
import tr.org.liderahenk.installer.lider.wizard.model.XmppNodeSwtModel;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class XmppClusterConfPage extends WizardPage implements IXmppPage {

	private LiderSetupConfig config;

	private Composite cmpMain;

	private Button btnAddRemoveNode;

	private Button btnUsePrivateKey;
	private Text txtPrivateKey;
	private Button btnUploadKey;
	private FileDialog dialog;
	private String selectedFile;
	private Text txtPassphrase;

	private Text txtServiceName;
	private Text txtXmppPort;
	private Text txtLdapServer;
	private Text txtLdapRootDn;
	private Text txtLdapRootPwd;
	private Text txtLdapBaseDn;
	private Text txtAdminPwd;
	private Text txtLiderUsername;
	private Text txtLiderUserPwd;

	private Text txtProxyAddress;
	private Text txtProxyPwd;

	private Map<Integer, XmppNodeSwtModel> nodeMap = new HashMap<Integer, XmppNodeSwtModel>();

	private Composite innerContainer;

	private NextPageEventType nextPageEventType = NextPageEventType.CLICK_FROM_PREV_PAGE;

	public XmppClusterConfPage(LiderSetupConfig config) {
		super(XmppClusterConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.2 " + Messages.getString("XMPP_CLUSTER_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		cmpMain = GUIHelper.createComposite(parent, 1);
		setControl(cmpMain);

		GUIHelper.createLabel(cmpMain, "");

		cmpMain = new ScrolledComposite(cmpMain, SWT.V_SCROLL);
		cmpMain.setLayout(new GridLayout(1, false));
		cmpMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		innerContainer = new Composite(cmpMain, SWT.NONE);
		innerContainer.setLayout(new GridLayout(1, false));
		innerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblGeneralInfo = GUIHelper.createLabel(innerContainer, Messages.getString("XMPP_CLUSTER_GENERAL_INFO"));
		lblGeneralInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		Composite cmpGeneralInfo = GUIHelper.createComposite(innerContainer, 2);
		cmpGeneralInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// General parameters' inputs
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("XMPP_SERVICE_NAME"));
		txtServiceName = GUIHelper.createText(cmpGeneralInfo);
		txtServiceName.setMessage(Messages.getString("ENTER_NAME_FOR_XMPP_SERVICE"));
		txtServiceName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SSH_PORT"));
		txtXmppPort = GUIHelper.createText(cmpGeneralInfo);
		txtXmppPort.setText("22");
		txtXmppPort.setMessage(Messages.getString("ENTER_PORT_FOR_SSH_CONNECTION"));
		txtXmppPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_SERVER_ADDRESS"));
		txtLdapServer = GUIHelper.createText(cmpGeneralInfo);
		txtLdapServer.setMessage(Messages.getString("ENTER_ADDRESS_OF_LDAP_SERVER"));
		txtLdapServer.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_ROOT_DN"));
		txtLdapRootDn = GUIHelper.createText(cmpGeneralInfo);
		txtLdapRootDn.setMessage(Messages.getString("ENTER_ROOT_DN_OF_LDAP"));
		txtLdapRootDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_ROOT_PWD"));
		txtLdapRootPwd = GUIHelper.createText(cmpGeneralInfo);
		txtLdapRootPwd.setMessage(Messages.getString("ENTER_LDAP_ROOT_PWD"));
		txtLdapRootPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LDAP_BASE_DN"));
		txtLdapBaseDn = GUIHelper.createText(cmpGeneralInfo);
		txtLdapBaseDn.setMessage(Messages.getString("ENTER_LDAP_BASE_DN"));
		txtLdapBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("EJABBERD_ADMIN_PWD"));
		txtAdminPwd = GUIHelper.createText(cmpGeneralInfo);
		txtAdminPwd.setMessage(Messages.getString("ENTER_PWD_FOR_EJABBERD_ADMIN"));
		txtAdminPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LIDER_SERVER_USERNAME"));
		txtLiderUsername = GUIHelper.createText(cmpGeneralInfo);
		txtLiderUsername.setMessage(Messages.getString("ENTER_USERNAME_FOR_LIDER_SERVER"));
		txtLiderUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("LIDER_SERVER_USER_PWD"));
		txtLiderUserPwd = GUIHelper.createText(cmpGeneralInfo);
		txtLiderUserPwd.setMessage(Messages.getString("ENTER_PWD_FOR_LIDER_SERVER_USER"));
		txtLiderUserPwd.addModifyListener(new ModifyListener() {
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

		Label lblNodeInfo = GUIHelper.createLabel(innerContainer, Messages.getString("XMPP_CLUSTER_NODE_INFO"));
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

		if (!txtServiceName.getText().isEmpty() && !txtXmppPort.getText().isEmpty()
				&& !txtLdapServer.getText().isEmpty() && !txtLdapRootDn.getText().isEmpty()
				&& !txtLdapRootPwd.getText().isEmpty() && !txtLdapBaseDn.getText().isEmpty()
				&& !txtProxyAddress.getText().isEmpty() && !txtLdapRootPwd.getText().isEmpty()
				&& (btnUsePrivateKey.getSelection() ? true : !txtProxyPwd.getText().isEmpty())) {

			for (Iterator<Entry<Integer, XmppNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
					.hasNext();) {
				Entry<Integer, XmppNodeSwtModel> entry = iterator.next();
				XmppNodeSwtModel node = entry.getValue();

				if (!node.getTxtNodeIp().getText().isEmpty() && !node.getTxtNodeName().getText().isEmpty()) {
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

		for (Iterator<Entry<Integer, XmppNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, XmppNodeSwtModel> entry = iterator.next();
			XmppNodeSwtModel node = entry.getValue();
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
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_NAME"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_USERNAME"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_PWD"));
			GUIHelper.createLabel(grpClusterNode, Messages.getString("NODE_EXISTS"));
		}
		
		XmppNodeSwtModel clusterNode = new XmppNodeSwtModel();

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

		Text txtNodeName = GUIHelper.createText(grpClusterNode);
		txtNodeName.setLayoutData(gd);
		txtNodeName.setMessage(Messages.getString("ENTER_NAME_FOR_THIS_NODE"));
		txtNodeName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeName(txtNodeName);

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
		txtNodeUsername.setEditable(false);

		Text txtNodeRootPwd = GUIHelper.createText(grpClusterNode);
		txtNodeRootPwd.setLayoutData(gd);
		txtNodeRootPwd.setMessage(Messages.getString("ENTER_ROOT_PWD_OF_THIS_NODE"));
		txtNodeRootPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		clusterNode.setTxtNodeRootPwd(txtNodeRootPwd);

		Button btnNodeNewSetup = new Button(grpClusterNode, SWT.CHECK | SWT.BORDER);
		// btnNodeNewSetup.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		btnNodeNewSetup.setSelection(false);
		btnNodeNewSetup.setToolTipText(Messages.getString("CHECK_IF_THIS_NODE_IS_ALREADY_INSTALLED"));
		clusterNode.setBtnNodeNewSetup(btnNodeNewSetup);

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

		config.setXmppHostname(txtServiceName.getText());
		config.setXmppPort(!txtXmppPort.getText().isEmpty() ? new Integer(txtXmppPort.getText()) : null);
		config.setXmppLdapServerAddress(txtLdapServer.getText());
		config.setXmppLdapRootDn(txtLdapRootDn.getText());
		config.setXmppLdapRootPwd(txtLdapRootPwd.getText());
		config.setXmppLdapBaseDn(txtLdapBaseDn.getText());
		config.setXmppProxyAddress(txtProxyAddress.getText());
		config.setXmppAdminPwd(txtAdminPwd.getText());
		config.setXmppLiderUsername(txtLiderUsername.getText());
		config.setXmppLiderPassword(txtLiderUserPwd.getText());
		config.setXmppNodeInfoMap(createInfoModelMap());

		if (btnUsePrivateKey.getSelection()) {
			config.setXmppAccessKeyPath(txtPrivateKey.getText());
			config.setXmppAccessPassphrase(txtPassphrase.getText());
			config.setXmppProxyPwd(null);
		} else {
			config.setXmppAccessKeyPath(null);
			config.setXmppAccessPassphrase(null);
			config.setXmppProxyPwd(txtProxyPwd.getText());
		}

	}

	private Map<Integer, XmppNodeInfoModel> createInfoModelMap() {
		Map<Integer, XmppNodeInfoModel> nodeInfoMap = new HashMap<Integer, XmppNodeInfoModel>();

		for (Iterator<Entry<Integer, XmppNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, XmppNodeSwtModel> entry = iterator.next();
			XmppNodeSwtModel nodeSwt = entry.getValue();

			XmppNodeInfoModel nodeInfo = new XmppNodeInfoModel(nodeSwt.getNodeNumber(),
					nodeSwt.getTxtNodeIp().getText(), nodeSwt.getTxtNodeName().getText(),
					nodeSwt.getTxtNodeUsername().getText(),
					btnUsePrivateKey.getSelection() ? null : nodeSwt.getTxtNodeRootPwd().getText(),
					!nodeSwt.getBtnNodeNewSetup().getSelection());

			nodeInfoMap.put(nodeSwt.getNodeNumber(), nodeInfo);
		}

		return nodeInfoMap;
	}

	private void setInputValues() {
		txtServiceName.setText("im." + config.getLdapOrgCn());
		txtLdapServer.setText(config.getLdapIp() != null ? config.getLdapIp() : "ldap." + config.getLdapOrgCn());
		if (config.isLdapUpdate()) {
			txtLdapRootDn.setText(config.getLdapAdminDn());
		} else {
			txtLdapRootDn.setText(
					config.getLdapAdminCn() != null ? "cn=" + config.getLdapAdminCn() + "," + config.getLdapBaseDn()
							: "cn=admin," + config.getLdapBaseDn());
		}
		txtLdapRootPwd.setText(config.getLdapAdminCnPwd() != null ? config.getLdapAdminCnPwd() : "secret");
		txtLdapBaseDn.setText(config.getLdapBaseDn());
		txtLiderUsername.setText("lider_sunucu");
		txtProxyAddress.setText("proxy." + config.getLdapOrgCn());
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
