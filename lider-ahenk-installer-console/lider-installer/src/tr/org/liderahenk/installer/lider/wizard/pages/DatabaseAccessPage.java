package tr.org.liderahenk.installer.lider.wizard.pages;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.dialogs.ConnectionCheckDialog;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class DatabaseAccessPage extends WizardPage implements IDatabasePage, ControlNextEvent {

	private LiderSetupConfig config;

	private Button btnUsernamePassword;
	private Text usernameTxt;
	private Text passwordTxt;
	private Button btnPrivateKey;
	private Text privateKeyTxt;
	private Button btnUploadKey;
	private FileDialog dialog;
	private String selectedFile;
	private Text passphraseTxt;

	private NextPageEventType nextPageEventType;

	// Set to true as default otherwise
	// wizard button activations does not work properly.
	private boolean goNextPage;

	private Text txtDatabaseRootPassword;

	public DatabaseAccessPage(LiderSetupConfig config) {
		super(DatabaseAccessPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.1 " + Messages.getString("DATABASE_ACCESS_FOR_INSTALLATION"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		// Access with username and password
		btnUsernamePassword = GUIHelper.createButton(mainContainer, SWT.RADIO,
				Messages.getString("WITH_USERNAME_AND_PASSWORD"));
		btnUsernamePassword.setSelection(true);

		// Creating a child container with two columns
		// and extra indent.
		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 30;
		Composite childContainer = GUIHelper.createComposite(mainContainer, gl, new GridData());

		GUIHelper.createLabel(childContainer, Messages.getString("USERNAME"));

		GridData gdForTextField = new GridData();
		gdForTextField.widthHint = 150;
		usernameTxt = GUIHelper.createText(childContainer, gdForTextField);
		// Force 'root' password until defconf-set-selections command is fixed
		usernameTxt.setText("root");
		usernameTxt.setEditable(false);

		GUIHelper.createLabel(childContainer, Messages.getString("PASSWORD"));

		// Creating password style text field.
		passwordTxt = GUIHelper.createText(childContainer, gdForTextField, SWT.NONE | SWT.BORDER | SWT.SINGLE);

		btnPrivateKey = GUIHelper.createButton(mainContainer, SWT.RADIO, Messages.getString("USE_PRIVATE_KEY"));

		// Creating another child container.
		Composite secondChild = GUIHelper.createComposite(mainContainer, gl, new GridData());

		GridData gdPrivateTxt = new GridData();
		gdPrivateTxt.widthHint = 250;
		privateKeyTxt = GUIHelper.createText(secondChild, gdPrivateTxt);

		// User should not be able to write
		// anything to this text field.
		privateKeyTxt.setEditable(false);

		// Create a dialog window.
		dialog = new FileDialog(mainContainer.getShell(), SWT.SAVE);
		dialog.setText(Messages.getString("UPLOAD_KEY"));

		btnUploadKey = GUIHelper.createButton(secondChild, SWT.PUSH, Messages.getString("UPLOAD_KEY"));

		// Creating another child container.
		Composite thirdChild = GUIHelper.createComposite(mainContainer, gl, new GridData());

		GUIHelper.createLabel(thirdChild, Messages.getString("PASSPHRASE(OPTIONAL)"));
		passphraseTxt = GUIHelper.createText(thirdChild, gdForTextField, SWT.NONE | SWT.BORDER | SWT.SINGLE);

		// Select user name and password option
		// as default.
		btnUsernamePassword.setSelection(true);

		Composite passwordComp = GUIHelper.createComposite(mainContainer, 2);

		GUIHelper.createLabel(passwordComp, Messages.getString("DATABASE_ROOT_PASSWORD"));

		txtDatabaseRootPassword = GUIHelper.createText(passwordComp);
		GridData gdRootPasswdTxt = new GridData();
		gdRootPasswdTxt.widthHint = 200;
		txtDatabaseRootPassword.setLayoutData(gdRootPasswdTxt);
		txtDatabaseRootPassword.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		// And organize fields according to
		// default selection.
		organizeFields();

		// Add selection listeners for
		// all text fields and buttons.
		btnUsernamePassword.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		usernameTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		passwordTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		btnPrivateKey.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

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

		setPageComplete(false);
	}

	/**
	 * This method organises widgets according to selections etc.
	 */
	private void organizeFields() {
		if (btnUsernamePassword.getSelection()) {
			usernameTxt.setEnabled(true);
			passwordTxt.setEnabled(true);
			btnUploadKey.setEnabled(false);
			privateKeyTxt.setEnabled(false);
			passphraseTxt.setEnabled(false);
		} else {
			usernameTxt.setEnabled(false);
			passwordTxt.setEnabled(false);
			btnUploadKey.setEnabled(true);
			privateKeyTxt.setEnabled(true);
			passphraseTxt.setEnabled(true);
		}
	}

	/**
	 * This method opens a dialog when triggered, and sets the private key text
	 * field.
	 */
	private void openDialog() {
		selectedFile = dialog.open();
		if (selectedFile != null && !"".equals(selectedFile)) {
			privateKeyTxt.setText(selectedFile);
		}
	}

	private void updatePageCompleteStatus() {
		if (btnUsernamePassword.getSelection()) {
			if (!LiderAhenkUtils.isEmpty(usernameTxt.getText()) && !LiderAhenkUtils.isEmpty(passwordTxt.getText())
					&& !txtDatabaseRootPassword.getText().isEmpty()) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		} else {
			if (!LiderAhenkUtils.isEmpty(privateKeyTxt.getText()) && !txtDatabaseRootPassword.getText().isEmpty()) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		}
	}

	/**
	 * This method sets info which taken from user to appropriate variables in
	 * LiderSetupConfig.
	 */
	private void setConfigVariables() {
		if (btnUsernamePassword.getSelection()) {
			config.setDatabaseAccessMethod(AccessMethod.USERNAME_PASSWORD);
			config.setDatabaseAccessUsername(usernameTxt.getText());
			config.setDatabaseAccessPasswd(passwordTxt.getText());
			config.setDatabaseRootPassword(txtDatabaseRootPassword.getText());
		} else {
			config.setDatabaseAccessMethod(AccessMethod.PRIVATE_KEY);
			config.setDatabaseAccessUsername(usernameTxt.getText());
			config.setDatabaseAccessKeyPath(privateKeyTxt.getText());
			config.setDatabaseAccessPassphrase(passphraseTxt.getText());
			config.setDatabaseRootPassword(txtDatabaseRootPassword.getText());
		}
	}

	@Override
	public IWizardPage getNextPage() {
		// Set config variables before going next page.
		setConfigVariables();

		// If next button clicked, open a dialog and start
		// authorization check.
		if (nextPageEventType == NextPageEventType.NEXT_BUTTON_CLICK) {
			openConnectionCheckDialog(getShell());
		}

		// Everytime this method runs, event type should be set to
		// NEXT_BUTTON_CLICK
		// otherwise we can never catch the real click to next button.
		nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;

		// goNextPage is the result of authorization check,
		// if we can authorize then go to next page
		// else do not allow.
		if (goNextPage == true) {
			return super.getNextPage();
		} else {
			return this;
		}
	}

	private void openConnectionCheckDialog(Shell shell) {
		// Create a dialog
		ConnectionCheckDialog ccDialog = new ConnectionCheckDialog(getShell(), config.getDatabaseIp(),
				config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(), config.getDatabaseAccessKeyPath(),
				config.getDatabaseAccessPassphrase(), config.getDatabaseAccessMethod());

		// Open it
		ccDialog.open();

		// After closing it get the result of authorization check.
		goNextPage = ccDialog.getCanAuthorize();
	}

	@Override
	public void setPageComplete(boolean complete) {
		// Set event type here. If event type is not set,
		// authorization check will be executed.
		nextPageEventType = NextPageEventType.SET_PAGE_COMPLETE;

		super.setPageComplete(complete);
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
