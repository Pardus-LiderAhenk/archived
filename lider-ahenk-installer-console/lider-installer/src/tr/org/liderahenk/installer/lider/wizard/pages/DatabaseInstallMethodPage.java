package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class DatabaseInstallMethodPage extends WizardPage implements IDatabasePage {

	private LiderSetupConfig config;

	private Button btnDebPackage;
	private Button btnWget;
	private Text txtFileName;
	private Button btnFileSelect;
	private FileDialog dialog;
	private Text txtDatabaseRootPassword;

	private Text downloadUrlTxt;

	private byte[] debContent;

	public DatabaseInstallMethodPage(LiderSetupConfig config) {
		super(DatabaseInstallMethodPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.2 " + Messages.getString("DATABASE_INSTALLATION_METHOD") + " - "
				+ Messages.getString("DB_SETUP_METHOD_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		btnDebPackage = GUIHelper.createButton(container, SWT.RADIO, Messages.getString("DB_SETUP_METHOD_DEB"));
		btnDebPackage.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				downloadUrlTxt.setEnabled(false);
				updateConfig();
				// Enable btnFileSelect only if btnDebPackage is selected
				btnFileSelect.setEnabled(btnDebPackage.getSelection());
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnDebPackage.setSelection(true);

		Group grpDebPackage = GUIHelper.createGroup(container, new GridLayout(2, false),
				new GridData(SWT.FILL, SWT.FILL, false, false));

		txtFileName = GUIHelper.createText(grpDebPackage, new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFileName.setEnabled(false); // do not let user to change it! It will
										// be updated on file selection

		// Copy mariadb.deb to /tmp and bring it as default deb in page
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("mariadb-server-10.1_10.1.16+maria-1-jessie_amd64.deb");
		File mariadbDeb = LiderAhenkUtils.streamToFile(inputStream, "mariadb-server-10.1_10.1.16+maria-1-jessie_amd64.deb");
		txtFileName.setText(mariadbDeb.getAbsolutePath());

		// Set file to config as array of bytes
		debContent = new byte[(int) mariadbDeb.length()];

		FileInputStream stream = null;
		try {
			stream = new FileInputStream(mariadbDeb);
			stream.read(debContent);
			stream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		config.setDatabaseDebFileContent(debContent);
		config.setDatabaseDebFileName(mariadbDeb.getAbsolutePath());

		// Upload deb package if necessary
		btnFileSelect = GUIHelper.createButton(grpDebPackage, SWT.NONE, Messages.getString("SELECT_FILE"));
		btnFileSelect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.deb" });
				dialog.setFilterNames(new String[] { "DEB" });

				String debFileName = dialog.open();
				if (debFileName != null) {

					txtFileName.setText(debFileName);
					File deb = new File(debFileName);
					debContent = new byte[(int) deb.length()];

					FileInputStream stream = null;
					try {
						stream = new FileInputStream(deb);
						stream.read(debContent);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							stream.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					// Set deb file
					config.setDatabaseDebFileName(debFileName);
					config.setDatabaseDebFileContent(debContent);
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnFileSelect.setEnabled(true);

		// Install by given URL
		btnWget = GUIHelper.createButton(container, SWT.RADIO, Messages.getString("DB_SETUP_METHOD_WGET"));
		btnWget.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnWget.getSelection()) {
					downloadUrlTxt.setEnabled(true);
					txtFileName.setEnabled(false);
					btnFileSelect.setEnabled(false);
					updateConfig();
					updatePageCompleteStatus();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Composite downloadUrlContainer = GUIHelper.createComposite(container, new GridLayout(1, false),
				new GridData(SWT.NO, SWT.NO, true, false));

		downloadUrlTxt = GUIHelper.createText(downloadUrlContainer);
		GridData gdDownloadUrlTxt = new GridData();
		gdDownloadUrlTxt.widthHint = 350;
		downloadUrlTxt.setLayoutData(gdDownloadUrlTxt);
		downloadUrlTxt.setEnabled(false);

		downloadUrlTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateConfig();
				updatePageCompleteStatus();
			}
		});

		Composite passwordComp = GUIHelper.createComposite(downloadUrlContainer, 2);

		GUIHelper.createLabel(passwordComp, Messages.getString("DATABASE_ROOT_PASSWORD"));

		txtDatabaseRootPassword = GUIHelper.createText(passwordComp);
		GridData gdRootPasswdTxt = new GridData();
		gdRootPasswdTxt.widthHint = 200;
		txtDatabaseRootPassword.setLayoutData(gdRootPasswdTxt);
		txtDatabaseRootPassword.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateConfig();
				updatePageCompleteStatus();
			}
		});

		Composite warningComp = GUIHelper.createComposite(downloadUrlContainer, 1);
		
		Label label1 = GUIHelper.createLabel(warningComp, "Kuruluma uygun deb dosyası varsayılan olarak getirilmiştir.");
		Label label2 = GUIHelper.createLabel(warningComp, "Hazır getirilen deb dosyasıyla kuruluma devam edebilirsiniz.");
		label1.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		label2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		updateConfig();
		updatePageCompleteStatus();
	}

	private void updateConfig() {
		if (btnDebPackage.getSelection()) {
			config.setDatabaseInstallMethod(InstallMethod.PROVIDED_DEB);
			config.setDatabasePackageName(null);
		} else {
			config.setDatabaseInstallMethod(InstallMethod.WGET);
			config.setDatabaseDownloadUrl(downloadUrlTxt.getText());
		}
		config.setDatabaseRootPassword(txtDatabaseRootPassword.getText());
	}

	private void updatePageCompleteStatus() {
		if (btnDebPackage.getSelection()) {
			setPageComplete(checkFile() && !txtDatabaseRootPassword.getText().isEmpty());
		} else {
			setPageComplete(!"".equals(downloadUrlTxt.getText()) && !txtDatabaseRootPassword.getText().isEmpty());
		}
	}

	private boolean checkFile() {
		return config.getDatabaseDebFileName() != null && config.getDatabaseDebFileContent() != null;
	}

	@Override
	public IWizardPage getPreviousPage() {

		((ControlNextEvent) super.getPreviousPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		return super.getPreviousPage();
	}

}
