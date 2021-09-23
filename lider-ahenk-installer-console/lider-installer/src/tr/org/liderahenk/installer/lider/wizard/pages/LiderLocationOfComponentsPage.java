package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.LinkedList;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.LiderSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LiderLocationOfComponentsPage extends WizardPage {

	private LiderSetupConfig config;

	private Button btnInstallCentral;
	private Button btnLocal;
	private Button btnRemote;
	private Text txtRemoteIp;
	private Text txtRemotePort;
	private Button btnInstallDistributed;
	private Text txtDatabaseIp;
	private Text txtLdapIp;
	private Text txtXmppIp;
	private Text txtLiderIp;
	private Text txtDatabasePort;
	private Text txtLdapPort;
	private Text txtXmppPort;
	private Text txtLiderPort;
	
	private Button btnDatabaseCluster;
	private Button btnXmppCluster;

	private Button btnLiderCluster;

	public LiderLocationOfComponentsPage(LiderSetupConfig config) {
		super(LiderLocationOfComponentsPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("1.2 " + Messages.getString("WHERE_TO_INSTALL_COMPONENTS"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		// Install to same computer
		btnInstallCentral = GUIHelper.createButton(mainContainer, SWT.RADIO,
				Messages.getString("INSTALL_ALL_COMPONENTS_TO_SAME_COMPUTER"));

		// Creating a child container with two columns
		// and extra indent.
		GridLayout gl = new GridLayout(3, false);
		gl.marginLeft = 30;
		Composite childContainer = GUIHelper.createComposite(mainContainer, gl, new GridData());

		// Install locally
		btnLocal = GUIHelper.createButton(childContainer, SWT.RADIO, Messages.getString("LOCAL_COMPUTER"));
		GUIHelper.createLabel(childContainer, "");
		GUIHelper.createLabel(childContainer, "");

		// Install to a remote computer
		btnRemote = GUIHelper.createButton(childContainer, SWT.RADIO, Messages.getString("REMOTE_COMPUTER"));

		// Creating a text field with width 150px.
		GridData gdForIpField = new GridData();
		gdForIpField.widthHint = 150;
		txtRemoteIp = GUIHelper.createText(childContainer, gdForIpField);

		GridData gdForPortField = new GridData();
		gdForPortField.widthHint = 50;
		gdForPortField.grabExcessHorizontalSpace = false;
		txtRemotePort = GUIHelper.createText(childContainer);
		txtRemotePort.setText("22");

		btnInstallDistributed = GUIHelper.createButton(mainContainer, SWT.RADIO,
				Messages.getString("INSTALL_COMPONENT_TO_DIFFERENT_COMPUTERS"));


		GridLayout glDatabase = new GridLayout(5, false);
		glDatabase.marginLeft = 30;
		Composite cmpDatabase = GUIHelper.createComposite(mainContainer, glDatabase, new GridData());

		GridData gdForLabels = new GridData();
		gdForLabels.widthHint = 140;
		
		// IP's for components will be taken in this section.
		Label lblDb = GUIHelper.createLabel(cmpDatabase, Messages.getString("DATABASE"));
		lblDb.setLayoutData(gdForLabels);
		
		txtDatabaseIp = GUIHelper.createText(cmpDatabase, gdForIpField);

		txtDatabasePort = GUIHelper.createText(cmpDatabase, gdForPortField);
		txtDatabasePort.setText("22");
		
		GUIHelper.createLabel(cmpDatabase, Messages.getString("DATABASE_CLUSTER"));
		
		btnDatabaseCluster = GUIHelper.createButton(cmpDatabase, SWT.CHECK | SWT.BORDER);
		btnDatabaseCluster.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnDatabaseCluster.getSelection()) {
					txtDatabaseIp.setEnabled(false);
					txtDatabasePort.setEnabled(false);
				} else {
					txtDatabaseIp.setEnabled(true);
					txtDatabasePort.setEnabled(true);
				}
				updatePageStatus(true);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		// Creating another child container.
		Composite secondChild = GUIHelper.createComposite(mainContainer, gl, new GridData());

		Label lblLdap = GUIHelper.createLabel(secondChild, Messages.getString("LDAP"));
		lblLdap.setLayoutData(gdForLabels);
		
		txtLdapIp = GUIHelper.createText(secondChild, gdForIpField);

		txtLdapPort = GUIHelper.createText(secondChild, gdForPortField);
		txtLdapPort.setText("22");

		GridLayout glXmpp= new GridLayout(5, false);
		glXmpp.marginLeft = 30;
		Composite cmpXmpp = GUIHelper.createComposite(mainContainer, glXmpp, new GridData());
		
		Label lblXmpp = GUIHelper.createLabel(cmpXmpp, Messages.getString("XMPP"));
		lblXmpp.setLayoutData(gdForLabels);
		
		txtXmppIp = GUIHelper.createText(cmpXmpp, gdForIpField);

		txtXmppPort = GUIHelper.createText(cmpXmpp, gdForPortField);
		txtXmppPort.setText("22");

		GUIHelper.createLabel(cmpXmpp, Messages.getString("XMPP_CLUSTER"));

		btnXmppCluster = GUIHelper.createButton(cmpXmpp, SWT.CHECK | SWT.BORDER);
		btnXmppCluster.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnXmppCluster.getSelection()) {
					txtXmppIp.setEnabled(false);
					txtXmppPort.setEnabled(false);
				} else {
					txtXmppIp.setEnabled(true);
					txtXmppPort.setEnabled(true);
				}
				updatePageStatus(true);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		GridLayout glLider = new GridLayout(5, false);
		glLider.marginLeft = 30;
		Composite cmpLider = GUIHelper.createComposite(mainContainer, glLider, new GridData());
		
		Label lblLider = GUIHelper.createLabel(cmpLider, Messages.getString("LIDER"));
		lblLider.setLayoutData(gdForLabels);
		
		txtLiderIp = GUIHelper.createText(cmpLider, gdForIpField);

		txtLiderPort = GUIHelper.createText(cmpLider, gdForPortField);
		txtLiderPort.setText("22");
		
		GUIHelper.createLabel(cmpLider, Messages.getString("LIDER_CLUSTER"));

		btnLiderCluster = GUIHelper.createButton(cmpLider, SWT.CHECK | SWT.BORDER);
		btnLiderCluster.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnLiderCluster.getSelection()) {
					txtLiderIp.setEnabled(false);
					txtLiderPort.setEnabled(false);
				} else {
					txtLiderIp.setEnabled(true);
					txtLiderPort.setEnabled(true);
				}
				updatePageStatus(true);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		// Adding selection listeners for
		// user's choices on radio buttons
		btnInstallCentral.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				updatePageStatus(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnInstallDistributed.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				updatePageStatus(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnLocal.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeInnerFields();
				updatePageStatus(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRemote.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeInnerFields();
				updatePageStatus(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// canNext must be triggered when IP text fields modified.
		txtRemoteIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageStatus(true);
			}
		});

		txtDatabaseIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageStatus(true);
			}
		});

		txtLdapIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageStatus(true);
			}
		});

		txtXmppIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageStatus(true);
			}
		});

		txtLiderIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageStatus(true);
			}
		});

		// Second option should come as selected
		// in the opening of page.
		btnInstallDistributed.setSelection(true);

		// This method sets fields enable/disable
		// according to user's radio button choices
		organizeFields();
		organizeClusterButtons();
		updatePageStatus(false);
	}

	// This method organizes button, fields etc.
	// according to selections etc.
	private void organizeFields() {
		if (btnInstallDistributed.getSelection()) {

			// Disable first option
			btnLocal.setEnabled(false);
			btnRemote.setEnabled(false);
			txtRemoteIp.setEnabled(false);
			txtRemotePort.setEnabled(false);

			// Enable second option
			if (config.isInstallDatabase()) {
				btnDatabaseCluster.setEnabled(true);
				if (btnDatabaseCluster.getSelection()) {
					txtDatabaseIp.setEnabled(false);
					txtDatabasePort.setEnabled(false);
				} else {
					txtDatabaseIp.setEnabled(true);
					txtDatabasePort.setEnabled(true);
				}
			}
			if (config.isInstallLdap()) {
				txtLdapIp.setEnabled(true);
				txtLdapPort.setEnabled(true);
			}
			if (config.isInstallXmpp()) {
				btnXmppCluster.setEnabled(true);
				if (btnXmppCluster.getSelection()) {
					txtXmppIp.setEnabled(false);
					txtXmppPort.setEnabled(false);
				} else {
					txtXmppIp.setEnabled(true);
					txtXmppPort.setEnabled(true);
				}
			}
			if (config.isInstallLider()) {
				btnLiderCluster.setEnabled(true);
				if (btnLiderCluster.getSelection()) {
					txtLiderIp.setEnabled(false);
					txtLiderPort.setEnabled(false);
				} else {
					txtLiderIp.setEnabled(true);
					txtLiderPort.setEnabled(true);
				}
			}
		} else {
			// Enable first option
			btnLocal.setEnabled(true);
			btnRemote.setEnabled(true);

			organizeInnerFields();

			// Disable second option
			txtDatabaseIp.setEnabled(false);
			txtDatabasePort.setEnabled(false);
			btnDatabaseCluster.setEnabled(false);
			btnXmppCluster.setEnabled(false);
			btnLiderCluster.setEnabled(false);
			txtLdapIp.setEnabled(false);
			txtLdapPort.setEnabled(false);
			txtXmppIp.setEnabled(false);
			txtXmppPort.setEnabled(false);
			txtLiderIp.setEnabled(false);
			txtLiderPort.setEnabled(false);
		}
	}

	private void organizeInnerFields() {
		// If it is the first selection
		// then select local installation as default
		if (btnInstallCentral.getSelection() && !btnRemote.getSelection()) {
			btnLocal.setSelection(true);
		}
		
		// If 'install to remote' is selected
		// then enable IP text field
		txtRemoteIp.setEnabled(btnRemote.getSelection());
		txtRemotePort.setEnabled(btnRemote.getSelection());
	}

	// This method decides to next button's status.
	private void updatePageStatus(boolean check) {
		if (!check) {
			setPageComplete(check);
		} else {
			if (btnInstallCentral.getSelection()) {
				if (btnLocal.getSelection()) {
					setPageComplete(true);
				} else if (btnRemote.getSelection() && NetworkUtils.isIpValid(txtRemoteIp.getText())) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			} else { // btnInstallDistributed
				setPageComplete(checkRequiredIps());
			}
		}
	}

	private boolean checkRequiredIps() {
		boolean databaseIpEntered = false; 
		boolean ldapIpEntered = false; 
		boolean xmppIpEntered = false; 
		boolean liderIpEntered = false; 
		
		if (config.isInstallDatabase() && !btnDatabaseCluster.getSelection()) {
			if (!txtDatabaseIp.getText().isEmpty() && NetworkUtils.isIpValid(txtDatabaseIp.getText())) {
				databaseIpEntered = true;
			} else {
				databaseIpEntered = false;
			}
		} else {
			databaseIpEntered = true;
		}
		if (config.isInstallLdap()) {
			if (!txtLdapIp.getText().isEmpty() && NetworkUtils.isIpValid(txtLdapIp.getText())) {
				ldapIpEntered = true;
			} else {
				ldapIpEntered = false;
			}
		}
		else {
			ldapIpEntered = true;
		}
		if (config.isInstallXmpp() && !btnXmppCluster.getSelection()) {
			if (!txtXmppIp.getText().isEmpty() && NetworkUtils.isIpValid(txtXmppIp.getText())) {
				xmppIpEntered = true;
			} else {
				xmppIpEntered = false;
			}
		}
		else {
			xmppIpEntered = true;
		}
		if (config.isInstallLider()  && !btnLiderCluster.getSelection()) {
			if (!txtLiderIp.getText().isEmpty() && NetworkUtils.isIpValid(txtLiderIp.getText())) {
				liderIpEntered = true;
			} else {
				liderIpEntered = false;
			}
		}
		else {
			liderIpEntered = true;
		}
		
		return (databaseIpEntered && ldapIpEntered && xmppIpEntered && liderIpEntered);
	}

	private void setConfigVariables() {
		// If components will be installed to same machine
		if (btnInstallCentral.getSelection()) {
			// If all components will be installed to localhost
			if (btnLocal.getSelection()) {
				// Set only selected components
				if (config.isInstallDatabase()) {
					config.setDatabaseIp("localhost");
					config.setDatabasePort(null);
					config.setDatabaseCluster(false);
				}
				if (config.isInstallLdap()) {
					config.setLdapIp("localhost");
					config.setLdapPort(null);
				}
				if (config.isInstallXmpp()) {
					config.setXmppIp("localhost");
					config.setXmppPort(null);
					config.setXmppCluster(false);
				}
				if (config.isInstallLider()) {
					config.setLiderIp("localhost");
					config.setLiderPort(null);
					config.setLiderCluster(false);
				}
			}
			// If all components will be installed to a remote computer
			else {
				// Set only selected components
				if (config.isInstallDatabase()) {
					config.setDatabaseIp(txtRemoteIp.getText());
					config.setDatabasePort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
					config.setDatabaseCluster(false);
				}
				if (config.isInstallLdap()) {
					config.setLdapIp(txtRemoteIp.getText());
					config.setLdapPort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
				}
				if (config.isInstallXmpp()) {
					config.setXmppIp(txtRemoteIp.getText());
					config.setXmppPort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
					config.setXmppCluster(false);
				}
				if (config.isInstallLider()) {
					config.setLiderIp(txtRemoteIp.getText());
					config.setLiderPort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
					config.setLiderCluster(false);
				}
			}
		}
		// If components will be installed distributed.
		else {
			// Set only selected components
			if (config.isInstallDatabase() && !btnDatabaseCluster.getSelection()) {
				config.setDatabaseIp(txtDatabaseIp.getText());
				config.setDatabasePort(txtDatabasePort.getText() != null && !txtDatabasePort.getText().isEmpty()
						? new Integer(txtDatabasePort.getText()) : null);
				config.setDatabaseCluster(false);
			} else if (config.isInstallDatabase() && btnDatabaseCluster.getSelection()) {
				config.setDatabaseCluster(true);
			}
			if (config.isInstallLdap()) {
				config.setLdapIp(txtLdapIp.getText());
				config.setLdapPort(txtLdapPort.getText() != null && !txtLdapPort.getText().isEmpty()
						? new Integer(txtLdapPort.getText()) : null);
			}
			if (config.isInstallXmpp() && !btnXmppCluster.getSelection()) {
				config.setXmppIp(txtXmppIp.getText());
				config.setXmppPort(txtXmppPort.getText() != null && !txtXmppPort.getText().isEmpty()
						? new Integer(txtXmppPort.getText()) : null);
				config.setXmppCluster(false);
			} else if (config.isInstallXmpp() && btnXmppCluster.getSelection()) {
				config.setXmppCluster(true);
			}
			if (config.isInstallLider() && !btnLiderCluster.getSelection()) {
				config.setLiderIp(txtLiderIp.getText());
				config.setLiderPort(txtLiderPort.getText() != null && !txtLiderPort.getText().isEmpty()
						? new Integer(txtLiderPort.getText()) : null);
				config.setLiderCluster(false);
			} else if (config.isInstallLider() && btnLiderCluster.getSelection()) {
				config.setLiderCluster(true);
			}
		}
	}

	/**
	 * This method decides next page according to user's component choices
	 * 
	 * @return
	 */
	private IWizardPage selectNextPage() {
		LinkedList<IWizardPage> pagesList = ((LiderSetupWizard) this.getWizard()).getPagesList();
		if (config.isInstallDatabase()) {
			if (config.isDatabaseCluster()) {
				return getWizard().getPage(DatabaseClusterConfPage.class.getName());
			} else {
				return getWizard().getPage(DatabaseAccessPage.class.getName());
			}
		} else if (config.isInstallLdap()) {
			return findFirstInstance(pagesList, ILdapPage.class);
		} else if (config.isInstallXmpp()) {
			if (config.isXmppCluster()) {
				return getWizard().getPage(XmppClusterConfPage.class.getName());
			} else {
				return getWizard().getPage(XmppAccessPage.class.getName());
			}
		} else if (config.isInstallLider()) { // Lider
			if (config.isLiderCluster()) {
				return getWizard().getPage(LiderClusterConfPage.class.getName());
			} else {
				return getWizard().getPage(LiderAccessPage.class.getName());
			}
		}
		return null;
	}

	/**
	 * Tries to find the first instance of the provided class in the linked
	 * list.
	 * 
	 * @param pagesList
	 * @param cls
	 * @return
	 */
	private IWizardPage findFirstInstance(LinkedList<IWizardPage> pagesList, Class<?> cls) {
		if (pagesList != null) {
			for (IWizardPage page : pagesList) {
				if (cls.isInstance(page)) {
					return page;
				}
			}
		}
		return null;
	}

	public void updatePage() {
		// If a component is not selected
		// then change its style to disabled.
		txtDatabaseIp.setEnabled(config.isInstallDatabase());
		txtDatabasePort.setEnabled(config.isInstallDatabase());
		txtLdapIp.setEnabled(config.isInstallLdap());
		txtLdapPort.setEnabled(config.isInstallLdap());
		txtXmppIp.setEnabled(config.isInstallXmpp());
		txtXmppPort.setEnabled(config.isInstallXmpp());
		txtLiderIp.setEnabled(config.isInstallLider());
		txtLiderPort.setEnabled(config.isInstallLider());
		
		organizeClusterButtons();
		
		organizeFields();
	}

	private void organizeClusterButtons() {
		if (config.isInstallDatabase()) {
			btnDatabaseCluster.setEnabled(true);
		} else {
			btnDatabaseCluster.setEnabled(false);
		}
		if (config.isInstallXmpp()) {
			btnXmppCluster.setEnabled(true);
		} else {
			btnXmppCluster.setEnabled(false);
		}
		if (config.isInstallLider()) {
			btnLiderCluster.setEnabled(true);
		} else {
			btnLiderCluster.setEnabled(false);
		}
	}
	
	@Override
	public IWizardPage getNextPage() {
		setConfigVariables();

		organizeClusterButtons();
		
//		((ControlNextEvent) selectNextPage()).setNextPageEventType(
//				NextPageEventType.CLICK_FROM_PREV_PAGE);
		
		return selectNextPage();
	}

}
