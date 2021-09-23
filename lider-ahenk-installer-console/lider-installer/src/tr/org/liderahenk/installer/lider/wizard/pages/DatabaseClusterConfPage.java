package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
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
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseNodeInfoModel;
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseNodeSwtModel;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class DatabaseClusterConfPage extends WizardPage implements IDatabasePage {

	private LiderSetupConfig config;

	private Composite cmpMain;

	private Button btnAddRemoveNode;

	private Text txtDbRootPwd;
	private Text txtDbSshPort;
	private Text txtClusterName;
	private Text txtSstUsername;
	private Text txtSstPwd;
	private Button btnUsePrivateKey;
	private Text txtPrivateKey;
	private Button btnUploadKey;
	private FileDialog dialog;
	private String selectedFile;
	private Text txtPassphrase;

	private Map<Integer, DatabaseNodeSwtModel> nodeMap = new HashMap<Integer, DatabaseNodeSwtModel>();

	public DatabaseClusterConfPage(LiderSetupConfig config) {
		super(DatabaseClusterConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.2 " + Messages.getString("DATABASE_CLUSTER_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		cmpMain = GUIHelper.createComposite(parent, 1);
		setControl(cmpMain);

		Label lblGeneralInfo = GUIHelper.createLabel(cmpMain, Messages.getString("MARIADB_CLUSTER_GENERAL_INFO"));
		lblGeneralInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		Composite cmpGeneralInfo = GUIHelper.createComposite(cmpMain, 2);
		cmpGeneralInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// General parameters' inputs
		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("MARIADB_CLUSTER_NAME"));
		txtClusterName = GUIHelper.createText(cmpGeneralInfo);
		txtClusterName.setText("MariaDB_Cluster");
		txtClusterName.setMessage(Messages.getString("ENTER_NAME_FOR_CLUSTER"));
		txtClusterName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("MARIA_DB_ROOT_PWD"));
		txtDbRootPwd = GUIHelper.createText(cmpGeneralInfo);
		txtDbRootPwd.setMessage(Messages.getString("ENTER_PWD_FOR_DB_ROOT_USER"));
		txtDbRootPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SSH_PORT"));
		txtDbSshPort = GUIHelper.createText(cmpGeneralInfo);
		txtDbSshPort.setText("22");
		txtDbSshPort.setMessage(Messages.getString("ENTER_PORT_FOR_SSH_CONNECTION"));
		txtDbSshPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SST_AUTH_USERNAME"));
		txtSstUsername = GUIHelper.createText(cmpGeneralInfo);
		txtSstUsername.setText("sst_user");
		txtSstUsername.setMessage(Messages.getString("ENTER_USERNAME_FOR_SST_USER"));
		txtSstUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpGeneralInfo, Messages.getString("SST_AUTH_PWD"));
		txtSstPwd = GUIHelper.createText(cmpGeneralInfo);
		txtSstPwd.setMessage(Messages.getString("ENTER_PWD_FOR_SST_USER"));
		txtSstPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		Composite cmpPrivateKey = GUIHelper.createComposite(cmpMain, 3);
		cmpPrivateKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// Create a dialog window.
		dialog = new FileDialog(cmpMain.getShell(), SWT.SAVE);
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

		Label lblNodeInfo = GUIHelper.createLabel(cmpMain, Messages.getString("MARIADB_CLUSTER_NODE_INFO"));
		lblNodeInfo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		Composite cmpNodeList = GUIHelper.createComposite(cmpMain, 2);
		cmpNodeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// Add at least 3 nodes
		createNewNode(cmpNodeList, true);
		GUIHelper.createLabel(cmpNodeList);
		createNewNode(cmpNodeList, false);
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
	}

	private void organizePasswordFields() {
		if (btnUsePrivateKey.getSelection()) {
			txtPrivateKey.setEnabled(true);
			btnUploadKey.setEnabled(true);
			txtPassphrase.setEnabled(true);
		} else {
			txtPrivateKey.setEnabled(false);
			btnUploadKey.setEnabled(false);
			txtPassphrase.setEnabled(false);
		}

		for (Iterator<Entry<Integer, DatabaseNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<Integer, DatabaseNodeSwtModel> entry = iterator.next();
			DatabaseNodeSwtModel node = entry.getValue();
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

		updatePageCompleteStatus();
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

		DatabaseNodeSwtModel clusterNode = new DatabaseNodeSwtModel();
		
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

		config.setDatabaseClusterAddress(createWsrepClusterAddress());
		config.setDatabaseClusterAddressForLider(createWsrepClusterAddressForLider());
		config.setDatabaseClusterName(txtClusterName.getText());
		config.setDatabaseRootPassword(txtDbRootPwd.getText());
		config.setDatabaseSstUsername(txtSstUsername.getText());
		config.setDatabaseSstPwd(txtSstPwd.getText());
		// config.setDatabaseNodeMap(nodeMap);
		config.setDatabasePort(txtDbSshPort.getText() != null ? new Integer(txtDbSshPort.getText()) : null);

		Map<Integer, DatabaseNodeInfoModel> nodeInfoMap = createInfoModelMap();

		config.setDatabaseNodeInfoMap(nodeInfoMap);

		if (btnUsePrivateKey.getSelection()) {
			config.setDatabaseAccessKeyPath(txtPrivateKey.getText());
			config.setDatabaseAccessPassphrase(txtPassphrase.getText());
		} else {
			config.setDatabaseAccessKeyPath(null);
			config.setDatabaseAccessPassphrase(null);
		}

	}

	private String createWsrepClusterAddress() {

		String wsrepClusterAddress = "";

		for (Iterator<Entry<Integer, DatabaseNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<Integer, DatabaseNodeSwtModel> entry = iterator.next();
			DatabaseNodeSwtModel clusterNode = entry.getValue();
			wsrepClusterAddress += clusterNode.getTxtNodeIp().getText() + ",";

		}

		// Delete last comma
		wsrepClusterAddress = wsrepClusterAddress.substring(0, wsrepClusterAddress.length() - 1);

		return wsrepClusterAddress;

	}

	private String createWsrepClusterAddressForLider() {

		String wsrepClusterAddress = "";

		for (Iterator<Entry<Integer, DatabaseNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<Integer, DatabaseNodeSwtModel> entry = iterator.next();
			DatabaseNodeSwtModel clusterNode = entry.getValue();
			wsrepClusterAddress += clusterNode.getTxtNodeIp().getText() + ":3306,";

		}

		// Delete last comma
		wsrepClusterAddress = wsrepClusterAddress.substring(0, wsrepClusterAddress.length() - 1);

		return wsrepClusterAddress;

	}

	private Map<Integer, DatabaseNodeInfoModel> createInfoModelMap() {
		Map<Integer, DatabaseNodeInfoModel> nodeInfoMap = new HashMap<Integer, DatabaseNodeInfoModel>();

		for (Iterator<Entry<Integer, DatabaseNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<Integer, DatabaseNodeSwtModel> entry = iterator.next();
			DatabaseNodeSwtModel nodeSwt = entry.getValue();

			DatabaseNodeInfoModel nodeInfo = new DatabaseNodeInfoModel(nodeSwt.getNodeNumber(),
					nodeSwt.getTxtNodeIp().getText(), nodeSwt.getTxtNodeName().getText(),
					nodeSwt.getTxtNodeUsername().getText(),
					btnUsePrivateKey.getSelection() ? null : nodeSwt.getTxtNodeRootPwd().getText(),
					!nodeSwt.getBtnNodeNewSetup().getSelection());

			nodeInfoMap.put(nodeSwt.getNodeNumber(), nodeInfo);
		}

		return nodeInfoMap;
	}

	private void updatePageCompleteStatus() {

		boolean pageComplete = false;

		if (!txtClusterName.getText().isEmpty() && !txtDbRootPwd.getText().isEmpty()
				&& !txtDbSshPort.getText().isEmpty() && !txtSstUsername.getText().isEmpty()
				&& !txtSstPwd.getText().isEmpty()) {
			for (Iterator<Entry<Integer, DatabaseNodeSwtModel>> iterator = nodeMap.entrySet().iterator(); iterator
					.hasNext();) {
				Entry<Integer, DatabaseNodeSwtModel> entry = iterator.next();
				DatabaseNodeSwtModel node = entry.getValue();

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

}
