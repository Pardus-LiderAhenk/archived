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
public class XmppInstallMethodPage extends WizardPage implements IXmppPage {

	private LiderSetupConfig config;

	// private Button btnAptGet;
	private Button btnDebPackage;
	private Button btnWget;
	private Text txtFileName;
	private Button btnFileSelect;
	private FileDialog dialog;

	private Text downloadUrlTxt;

	private byte[] debContent;

	public XmppInstallMethodPage(LiderSetupConfig config) {
		super(XmppInstallMethodPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("4.2 " + Messages.getString("XMPP_INSTALLATION_METHOD") + " - "
				+ Messages.getString("DB_SETUP_METHOD_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		// Ask user if Xmpp will be installed from a .deb package or via
		// apt-get
		// btnAptGet = GUIHelper.createButton(container, SWT.RADIO,
		// Messages.getString("XMPP_SETUP_METHOD_APT_GET"));
		// btnAptGet.addSelectionListener(new SelectionListener() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// downloadUrlTxt.setEnabled(false);
		// updateConfig();
		// updatePageCompleteStatus();
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// }
		// });
		// btnAptGet.setSelection(true);

		btnDebPackage = GUIHelper.createButton(container, SWT.RADIO, Messages.getString("XMPP_SETUP_METHOD_DEB"));
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

		// Copy ejabberd.deb to /tmp and bring it as default deb in page
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ejabberd_16.06-0_amd64.deb");
		File ejabberdDeb = LiderAhenkUtils.streamToFile(inputStream, "ejabberd_16.06-0_amd64.deb");
		txtFileName.setText(ejabberdDeb.getAbsolutePath());

		// Set file to config as array of bytes
		debContent = new byte[(int) ejabberdDeb.length()];

		FileInputStream stream = null;
		try {
			stream = new FileInputStream(ejabberdDeb);
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

		config.setXmppDebFileContent(debContent);
		config.setXmppDebFileName(ejabberdDeb.getAbsolutePath());

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

					// Set deb file
					config.setXmppDebFileName(debFileName);
					config.setXmppDebFileContent(debContent);
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnFileSelect.setEnabled(true);

		// Install by given URL
		btnWget = GUIHelper.createButton(container, SWT.RADIO, Messages.getString("XMPP_INSTALL_FROM_GIVEN_URL"));
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

		Composite downloadUrlContainer = GUIHelper.createComposite(container, 1);
		GridLayout glDownloadUrl = new GridLayout(1, false);
		downloadUrlContainer.setLayout(glDownloadUrl);

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

		Composite warningComp = GUIHelper.createComposite(downloadUrlContainer, 1);

		Label label = GUIHelper.createLabel(warningComp,
				"Ejabberd versiyon 16.02 depolarda bulunmamaktadır.\nBu nedenle Ejabberd kurulumu sadece DEB dosyasından veya link üzerinden yapılabilir.\nKuruluma uygun deb dosyası varsayılan olarak getirilmiştir.");
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		updateConfig();
		updatePageCompleteStatus();
	}

	private void updatePageCompleteStatus() {
		// if (btnAptGet.getSelection()) {
		// setPageComplete(true);
		// } else if (btnDebPackage.getSelection()) {
		// setPageComplete(checkFile());
		// } else {
		// setPageComplete(!"".equals(downloadUrlTxt.getText()));
		// }
		if (btnDebPackage.getSelection()) {
			setPageComplete(checkFile());
		} else {
			setPageComplete(!"".equals(downloadUrlTxt.getText()));
		}
	}

	private boolean checkFile() {
		return config.getXmppDebFileName() != null && config.getXmppDebFileContent() != null;
	}

	private void updateConfig() {
		if (btnDebPackage.getSelection()) {
			config.setXmppInstallMethod(InstallMethod.PROVIDED_DEB);
			config.setXmppPackageName(null);
		}
		// else if (btnAptGet.getSelection()) {
		// config.setXmppInstallMethod(InstallMethod.APT_GET);
		// config.setXmppPackageName(PropertyReader.property("xmpp.package.name"));
		// }
		else {
			config.setXmppInstallMethod(InstallMethod.WGET);
			config.setXmppDownloadUrl(downloadUrlTxt.getText());
		}
	}

	@Override
	public IWizardPage getPreviousPage() {

		// If previous page is XmppAccessPage, set NextPageEventType. Otherwise
		// authorization check will start.
		if (super.getPreviousPage().getName().equals(XmppAccessPage.class.getName())) {
			((ControlNextEvent) super.getPreviousPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);
		}

		return super.getPreviousPage();
	}

	@Override
	public IWizardPage getNextPage() {
		
		// I don't know why but XmppConfPage's getNextPage method is not
		// triggered automatically. So I did it manually.
		XmppConfPage confPage = (XmppConfPage) super.getNextPage();
		confPage.setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);
		confPage.getNextPage();
		return confPage;
	}

}
