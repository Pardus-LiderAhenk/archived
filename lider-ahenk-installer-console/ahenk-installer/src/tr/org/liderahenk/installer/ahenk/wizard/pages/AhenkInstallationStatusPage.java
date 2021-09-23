package tr.org.liderahenk.installer.ahenk.wizard.pages;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.IOutputStreamProvider;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * 
 * 
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 * @author Volkan Şahin <bm.volkansahin@gmail.com>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class AhenkInstallationStatusPage extends WizardPage implements ControlNextEvent, InstallationStatusPage {

	private AhenkSetupConfig config = null;

	// Widgets
	private Composite mainContainer = null;
	private ProgressBar progressBar;
	private Text txtLogConsole;
	private NextPageEventType nextPageEventType;
	boolean isInstallationFinished = false;
	boolean canGoBack = false;
	private int progressBarPercent;

	private final static String MOVE_SOURCES_LIST = "sudo mv -f /tmp/liderahenk.list /etc/apt/sources.list.d/";
	private final static String UPDATE_PACKAGE_LIST = "sudo apt-get update";
	private final static String REMOVE_AHENK_PACKAGE = "sudo apt-get purge --auto-remove -y --force-yes ahenk ahenk-*";
	private final static String INSTALL_AHENK_PACKAGE = "sudo apt-get install -y --force-yes gdebi && sudo gdebi -n /tmp/{0}";
	private final static String MOVE_AHENK_CONF = "sudo mv -f /tmp/{0} /etc/ahenk/";
	private final static String RESTART_AHENK_SERVICE = "sudo service ahenk restart";
	private static final String DOWNLOAD_PACKAGE = "wget --output-document=/tmp/{0} {1}";
	private static final String DEBCONF_SET_SELECTIONS = "sudo debconf-set-selections <<< 'libpam-runtime libpam-runtime/override boolean true'";

	public AhenkInstallationStatusPage(AhenkSetupConfig config) {
		super(AhenkInstallationStatusPage.class.getName(), Messages.getString("INSTALLATION_OF_AHENK"), null);
		setDescription(Messages.getString("INSTALLATION_STATUS"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		txtLogConsole = GUIHelper.createText(mainContainer, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		progressBar = new ProgressBar(mainContainer, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);

		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {
		// Start Ahenk installation here. To prevent triggering installation
		// again, set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			canGoBack = false;

			// Create a thread pool
			final ExecutorService executor = Executors.newCachedThreadPool();
			setProgressBar(10, Display.getCurrent());
			printMessage(Messages.getString("INITIALIZING_INSTALLATION"), Display.getCurrent());
			// Get display before main runnable
			final Display display = Display.getCurrent();

			// Create a main runnable and execute installations as new runnables
			// under this one. Because at the end of installation we have to
			// wait
			// until all runnables completed and this situation locks GUI.
			Runnable mainRunnable = new Runnable() {
				@Override
				public void run() {

					// Calculate progress bar increment size
					final Integer increment = (Integer) (90 / config.getIpList().size());

					// Check installation method
					if (config.getAhenkInstallMethod() != InstallMethod.PROVIDED_DEB
							&& config.getAhenkInstallMethod() != InstallMethod.WGET) {
						// If installation method is not set, show an error
						// message and do not try to install
						isInstallationFinished = false;

						// If any error occured user should be able to go back
						// and change selections etc.
						canGoBack = true;

						// Set progress bar to complete
						setProgressBar(100, Display.getCurrent());

						printMessage("Invalid installation method. Installation cancelled.", Display.getCurrent());
						return;
					}

					// Create new runnable for each IP address...
					for (final String ip : config.getIpList()) {
						// Execute each installation in a new runnable.
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								try {
									// Check authorization before starting
									// installation
									printMessage(Messages.getString("TRYING_TO_CONNECT_TO", ip), display);
									final boolean canConnect = SetupUtils.canConnectViaSsh(ip, config.getUsernameCm(),
											config.getPasswordCm(), config.getPort(), config.getPrivateKeyAbsPath(),
											config.getPassphrase());
									if (canConnect) {
										printMessage(Messages.getString("SUCCESSFULLY_CONNECTED_TO", ip), display);

										try {
											// Add Lider Ahenk repository
											printMessage(Messages.getString("ADDING_REQUIRED_REPO_AT", ip), display);
											SetupUtils
													.copyFile(ip, config.getUsernameCm(), config.getPasswordCm(),
															config.getPort(), config.getPrivateKeyAbsPath(),
															config.getPassphrase(),
															LiderAhenkUtils.streamToFile(
																	this.getClass().getResourceAsStream(
																			"/conf/liderahenk.list"),
																	"liderahenk.list"),
															"/tmp/");
											SetupUtils.executeCommand(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(),
													MOVE_SOURCES_LIST, new IOutputStreamProvider() {
														@Override
														public byte[] getStreamAsByteArray() {
															return (config.getPasswordCm() + "\n")
																	.getBytes(StandardCharsets.UTF_8);
														}
													});

											// Update package list
											SetupUtils.executeCommand(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(),
													UPDATE_PACKAGE_LIST, new IOutputStreamProvider() {
														@Override
														public byte[] getStreamAsByteArray() {
															return (config.getPasswordCm() + "\n")
																	.getBytes(StandardCharsets.UTF_8);
														}
													});
											printMessage(Messages.getString("SUCCESSFULLY_ADDED_REQUIRED_REPO_AT", ip),
													display);
										} catch (Exception e) {
											printMessage(Messages.getString(
													"EXCEPTION_OCCURED_WHILE_ADDING_NEW_REPO_AT", ip), display);
										}

										try {
											// Remove previous Ahenk
											SetupUtils.executeCommand(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(),
													REMOVE_AHENK_PACKAGE, new IOutputStreamProvider() {
														@Override
														public byte[] getStreamAsByteArray() {
															return (config.getPasswordCm() + "\n")
																	.getBytes(StandardCharsets.UTF_8);
														}
													});
										} catch (Exception e) {
											// TODO ignore e100 only!
										}

										String filename = null;
										if (config.getAhenkInstallMethod() == InstallMethod.PROVIDED_DEB) {
											// Copy Ahenk deb file
											File debFile = new File(config.getDebFileAbsPath());
											SetupUtils.copyFile(ip, config.getUsernameCm(), config.getPasswordCm(),
													config.getPort(), config.getPrivateKeyAbsPath(),
													config.getPassphrase(), debFile, "/tmp/");
											filename = debFile.getName();
											printMessage("Successfully copied file", display);
										} else if (config.getAhenkInstallMethod() == InstallMethod.WGET) {
											// Download Ahenk deb file
											printMessage("Downloading Ahenk .deb package from: "
													+ config.getAhenkDownloadUrl(), display);
											SetupUtils.executeCommand(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(),
													config.getPassphrase(), DOWNLOAD_PACKAGE.replace("{0}", "ahenk.deb")
															.replace("{1}", config.getAhenkDownloadUrl()),
													new IOutputStreamProvider() {
														@Override
														public byte[] getStreamAsByteArray() {
															return (config.getPasswordCm() + "\n")
																	.getBytes(StandardCharsets.UTF_8);
														}
													});
											filename = "ahenk.deb";
											printMessage("Successfully downloaded file", display);
										}

										printMessage(Messages.getString("SETTING_DEBCONF_SELECTIONS_", ip), display);
										SetupUtils.executeCommand(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												DEBCONF_SET_SELECTIONS,
												new IOutputStreamProvider() {
													@Override
													public byte[] getStreamAsByteArray() {
														return (config.getPasswordCm() + "\n")
																.getBytes(StandardCharsets.UTF_8);
													}
												});
										printMessage(Messages.getString("SUCCESSFULLY_SET_DEBCONF_SELECTIONS_", ip),
												display);

										// Install Ahenk
										printMessage(Messages.getString("INSTALLING_AHENK_AT", ip), display);
										SetupUtils.executeCommand(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												INSTALL_AHENK_PACKAGE.replace("{0}", filename),
												new IOutputStreamProvider() {
													@Override
													public byte[] getStreamAsByteArray() {
														return (config.getPasswordCm() + "\n")
																.getBytes(StandardCharsets.UTF_8);
													}
												});
										printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_AHENK_AT", ip),
												display);

										// Copy configuration files
										printMessage(Messages.getString("COPYING_CONFIGURATION_FILES_TO", ip), display);
										final File fileConf = new File(config.getAhenkAbsPathConfFile());
										SetupUtils.copyFile(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												fileConf, "/tmp/");
										SetupUtils.executeCommand(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												MOVE_AHENK_CONF.replace("{0}", fileConf.getName()),
												new IOutputStreamProvider() {
													@Override
													public byte[] getStreamAsByteArray() {
														return (config.getPasswordCm() + "\n")
																.getBytes(StandardCharsets.UTF_8);
													}
												});
										final File logConf = new File(config.getAhenkLogConfAbsPath());
										SetupUtils.copyFile(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												logConf, "/tmp/");
										SetupUtils.executeCommand(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												MOVE_AHENK_CONF.replace("{0}", logConf.getName()),
												new IOutputStreamProvider() {
													@Override
													public byte[] getStreamAsByteArray() {
														return (config.getPasswordCm() + "\n")
																.getBytes(StandardCharsets.UTF_8);
													}
												});
										printMessage(
												Messages.getString("SUCCESSFULLY_COPIED_CONFIGURATION_FILES_TO", ip),
												display);

										// Restart Ahenk service
										printMessage(Messages.getString("STARTING_AHENK_SERVICE_AT", ip), display);
										SetupUtils.executeCommand(ip, config.getUsernameCm(), config.getPasswordCm(),
												config.getPort(), config.getPrivateKeyAbsPath(), config.getPassphrase(),
												RESTART_AHENK_SERVICE, new IOutputStreamProvider() {
													@Override
													public byte[] getStreamAsByteArray() {
														return (config.getPasswordCm() + "\n")
																.getBytes(StandardCharsets.UTF_8);
													}
												});

										printMessage(Messages.getString("SUCCESSFULLY_STARTED_AHENK_SERVICE_AT", ip),
												display);
										setProgressBar(increment, display);
									} else {
										printMessage(Messages.getString("COULD_NOT_CONNECT_TO_PASSING_OVER", ip),
												display);
										setProgressBar(increment, display);
									}
								} catch (SSHConnectionException e) {
									// Also update progress bar when
									// installation fails
									setProgressBar(increment, display);
									isInstallationFinished = false;
									// If any error occured user should be
									// able to go back and change selections
									// etc.
									canGoBack = true;
									printMessage("Error occurred: " + e.getMessage(), display);
									e.printStackTrace();
								} catch (CommandExecutionException e) {
									// Also update progress bar when
									// installation fails
									setProgressBar(increment, display);
									isInstallationFinished = false;
									// If any error occured user should be
									// able to go back and change selections
									// etc.
									canGoBack = true;
									printMessage("Error occurred: " + e.getMessage(), display);
									e.printStackTrace();
								}
							}
						};

						executor.execute(runnable);
					}

					// Await termination
					try {
						executor.shutdown();
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					isInstallationFinished = true;
					// Set progress bar to complete
					setProgressBar(100, display);
					printMessage(Messages.getString("INSTALLATION_COMPLETED"), display);
					config.setInstallationFinished(isInstallationFinished);
					// To enable finish button
					setPageCompleteAsync(isInstallationFinished, display);
				}
			};

			Thread thread = new Thread(mainRunnable);
			thread.start();
		}

		return super.getNextPage();
	}

	/**
	 * Prints log message to the log console widget
	 * 
	 * @param message
	 */
	private void printMessage(final String message, Display display) {
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
	 * Sets progress bar selection (Increases progress bar percentage by
	 * increment value.)
	 * 
	 * @param selection
	 */
	private void setProgressBar(final int increment, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progressBarPercent += increment;
				progressBar.setSelection(progressBarPercent);
			}
		});
	}

	/**
	 * Sets page complete status asynchronously.
	 * 
	 * @param isComplete
	 */
	private void setPageCompleteAsync(final boolean isComplete, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				setPageComplete(isComplete);
			}
		});
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
		return nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
