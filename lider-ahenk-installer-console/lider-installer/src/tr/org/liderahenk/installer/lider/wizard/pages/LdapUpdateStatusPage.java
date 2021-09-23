package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.io.InputStream;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.utils.PageFlowHelper;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LdapUpdateStatusPage extends WizardPage implements ILdapPage, InstallationStatusPage, ControlNextEvent {
	
	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;
	
	public LdapUpdateStatusPage(LiderSetupConfig config) {
		super(LdapUpdateStatusPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("LDAP_UPDATE"));
		this.config = config;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		txtLogConsole = GUIHelper.createText(container, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		progressBar = new ProgressBar(container, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		// progressGd.widthHint = 780;
		progressBar.setLayoutData(progressGd);
	}
	
	@Override
	public IWizardPage getNextPage() {
		// Start LDAP installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			final Display display = Display.getCurrent();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					setPageCompleteAsync(isInstallationFinished);
					
					printMessage("Initializing configuration...");
					setProgressBar(10);
					
					File ldapUpdateFile;
					try {
						ldapUpdateFile = new File(config.getLdapAbsPathConfFile());
						
						InputStream inputStream = this.getClass().getClassLoader()
								.getResourceAsStream("liderahenk.ldif");
						File liderAhenkLdifFile = LiderAhenkUtils.streamToFile(inputStream, "liderahenk.ldif");
						setProgressBar(25);
						
						printMessage(Messages.getString("SENDING_LDIF"));
						// Send liderahenk.ldif
						SetupUtils.copyFile(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(), config.getLdapAccessKeyPath(),
								config.getLdapAccessPassphrase(), liderAhenkLdifFile, "/tmp/");
						setProgressBar(40);

						printMessage(Messages.getString("SENDING_CONFIG_SCRIPT"));
						// Send LDAP config script
						SetupUtils.copyFile(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(), config.getLdapAccessKeyPath(),
								config.getLdapAccessPassphrase(), ldapUpdateFile, "/tmp/");
						setProgressBar(55);
						
						printMessage(Messages.getString("MAKING_SCRIPT_EXECUTABLE"));
						// Maket it executable
						SetupUtils.executeCommand(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(), config.getLdapAccessKeyPath(),
								config.getLdapAccessPassphrase(), "chmod +x /tmp/" + ldapUpdateFile.getName());
						setProgressBar(70);

						printMessage(Messages.getString("RUNNING_LDAP_CONFIG"));
						// Run LDAP config script
						SetupUtils.executeCommand(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(), config.getLdapAccessKeyPath(),
								config.getLdapAccessPassphrase(), "/tmp/" + ldapUpdateFile.getName());
						setProgressBar(85);
						
						printMessage("LDAP configuration completed successfully.");
						
					} catch (CommandExecutionException e) {
						isInstallationFinished = false;
						canGoBack = true;
						printMessage("Error occurred: " + e.getMessage());
						e.printStackTrace();
					} catch (SSHConnectionException e) {
						isInstallationFinished = false;
						canGoBack = true;
						printMessage("Error occurred: " + e.getMessage());
						e.printStackTrace();
					}
					
					setProgressBar(100);

					isInstallationFinished = true;
					
					config.setInstallationFinished(isInstallationFinished);

					setPageCompleteAsync(isInstallationFinished);

				}

				/**
				 * Prints log message to the log console widget
				 * 
				 * @param message
				 */
				private void printMessage(final String message) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
									? txtLogConsole.getText() + "\n" : "") + message);
						}
					});
				}

				/**
				 * Sets progress bar selection
				 * 
				 * @param selection
				 */
				private void setProgressBar(final int selection) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setSelection(selection);
						}
					});
				}

				/**
				 * Sets page complete status asynchronously.
				 * 
				 * @param isComplete
				 */
				private void setPageCompleteAsync(final boolean isComplete) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							setPageComplete(isComplete);
						}
					});
				}

			};

			Thread thread = new Thread(runnable);
			thread.start();
		}

		// Select next page.
		return PageFlowHelper.selectNextPage(config, this);
	}
	
	@Override
	public IWizardPage getPreviousPage() {
		// Do not allow to go back from this page if installation completed
		// successfully.
		if (canGoBack) {
			return super.getPreviousPage();
		} else {
			return null;
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
