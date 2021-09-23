package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.installer.lider.callables.DatabaseOnlyConfigureNodeCallable;
import tr.org.liderahenk.installer.lider.callables.DatabaseSetupClusterNodeCallable;
import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.utils.PageFlowHelper;
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseNodeInfoModel;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class DatabaseClusterInstallationStatus extends WizardPage
		implements IDatabasePage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

	private static final Logger logger = LoggerFactory.getLogger(DatabaseClusterInstallationStatus.class);

	public DatabaseClusterInstallationStatus(LiderSetupConfig config) {
		super(DatabaseClusterInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.4 " + Messages.getString("DATABASE_CLUSTER_INSTALLATION"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		txtLogConsole = GUIHelper.createText(container, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtLogConsole.setTopIndex(txtLogConsole.getLineCount() - 1);

		progressBar = new ProgressBar(container, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		// progressGd.widthHint = 780;
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {

		// Start database installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			canGoBack = false;

			progressBar.setVisible(true);

			// Get display before new main runnable
			final Display display = Display.getCurrent();

			setPageCompleteAsync(isInstallationFinished, display);

			clearLogConsole(display);

			// Create a thread pool
			final ExecutorService executor = Executors.newFixedThreadPool(10);

			// Create future list that will keep the results of callables.
			final List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();

			printMessage(Messages.getString("INITIALIZING_INSTALLATION"), display);

			// Create a main runnable and execute installations as new runnables
			// under this one. Because at the end of installation I have to wait
			// until all runnables completed and this situation locks GUI.
			Runnable mainRunnable = new Runnable() {
				@Override
				public void run() {
					for (Iterator<Entry<Integer, DatabaseNodeInfoModel>> iterator = config.getDatabaseNodeInfoMap()
							.entrySet().iterator(); iterator.hasNext();) {

						Entry<Integer, DatabaseNodeInfoModel> entry = iterator.next();
						final DatabaseNodeInfoModel clusterNode = entry.getValue();

						if (clusterNode.isNodeNewSetup()) {
							Callable<Boolean> callable = new DatabaseSetupClusterNodeCallable(clusterNode.getNodeIp(),
									clusterNode.getNodeRootPwd(), clusterNode.getNodeName(), display, config,
									txtLogConsole);
							Future<Boolean> result = executor.submit(callable);
							resultList.add(result);
						} else {
							Callable<Boolean> callable = new DatabaseOnlyConfigureNodeCallable(clusterNode.getNodeIp(),
									clusterNode.getNodeRootPwd(), clusterNode.getNodeName(), display, config,
									txtLogConsole);
							Future<Boolean> result = executor.submit(callable);
							resultList.add(result);
						}
					}

					try {
						executor.shutdown();
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					boolean allNodesSuccess = false;

					if (resultList.size() > 0) {
						// Check if all nodes are properly installed
						for (Future<Boolean> future : resultList) {
							try {
								allNodesSuccess = future.get();
								if (!allNodesSuccess) {
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
								allNodesSuccess = false;
								break;
							}
						}
					} else {
						allNodesSuccess = true;
					}

					if (allNodesSuccess) {

						try {
							// Get first node
							DatabaseNodeInfoModel firstNode = config.getDatabaseNodeInfoMap().get(1);

							if (config.getDatabaseAccessKeyPath() == null) {
								for (Iterator<Entry<Integer, DatabaseNodeInfoModel>> iterator = config
										.getDatabaseNodeInfoMap().entrySet().iterator(); iterator.hasNext();) {

									Entry<Integer, DatabaseNodeInfoModel> entry = iterator.next();
									final DatabaseNodeInfoModel clusterNode = entry.getValue();

									if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
										// Send debian.cnf from first node to
										// others
										configureNodeFromNode(firstNode, clusterNode, display);
									}
								}
							} else {

								// Change password parameter in debian.cnf of
								// all nodes (they must be same)
								for (Iterator<Entry<Integer, DatabaseNodeInfoModel>> iterator = config
										.getDatabaseNodeInfoMap().entrySet().iterator(); iterator.hasNext();) {

									Entry<Integer, DatabaseNodeInfoModel> entry = iterator.next();
									final DatabaseNodeInfoModel clusterNode = entry.getValue();

									configureNodeFromLocal(clusterNode, display);
								}
							}

							// Start first node
							startFirstNode(firstNode, display);

							// And start other nodes.
							for (Iterator<Entry<Integer, DatabaseNodeInfoModel>> iterator = config
									.getDatabaseNodeInfoMap().entrySet().iterator(); iterator.hasNext();) {

								Entry<Integer, DatabaseNodeInfoModel> entry = iterator.next();
								final DatabaseNodeInfoModel clusterNode = entry.getValue();

								if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
									// start other nodes.
									startNode(clusterNode, display);
								}
							}

							canGoBack = false;

							isInstallationFinished = true;

							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									progressBar.setVisible(false);
								}
							});

							printMessage(Messages.getString("MARIADB_GALERA_INSTALLATION_FINISHED"), display);

							config.setInstallationFinished(isInstallationFinished);

							setPageCompleteAsync(isInstallationFinished, display);

						} catch (Exception e) {
							e.printStackTrace();
							printMessage(Messages.getString("ERROR_OCCURED_WHILE_STARTING_OR_CONFIGURING_NODE"),
									display);
							printMessage(Messages.getString("ERROR_MESSAGE_",e.getMessage()), display);
							isInstallationFinished = false;
							// If any error occured user should be
							// able to go back and change selections
							// etc.
							canGoBack = true;
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									progressBar.setVisible(false);
								}
							});
						}

					} else {
						printMessage(Messages.getString("INSTALLER_WONT_CONTINUE_BECAUSE_ONE_OF_NODES_SETUP_FAILED"),
								display);
						isInstallationFinished = false;

						// If any error occured user should be
						// able to go back and change selections
						// etc.
						canGoBack = true;
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								progressBar.setVisible(false);
							}
						});

						setPageCompleteAsync(isInstallationFinished, display);
						
						if (!isInstallationFinished) {
							try {
								openDownloadUrl();
							} catch (Exception e) {
								e.printStackTrace();
								txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
										? txtLogConsole.getText() + "\n" : "")
										+ Messages.getString("CANNOT_OPEN_BROWSER_PLEASE_GO_TO") + "\n"
										+ PropertyReader.property("troubleshooting.url"));
							}
						}
					}

				}
			};
			Thread thread = new Thread(mainRunnable);
			thread.start();

		}
		// Select next page.
		return PageFlowHelper.selectNextPage(config, this);
	}
	
	private void openDownloadUrl() throws IOException {
		Runtime.getRuntime().exec("xdg-open " + PropertyReader.property("troubleshooting.url"));
	}

	private void startFirstNode(DatabaseNodeInfoModel firstNode, Display display) throws Exception {

		SSHManager manager = null;

		try {
			printMessage(Messages.getString("CONNECTING_TO_", firstNode.getNodeIp()), display);
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(),
					config.getDatabasePort(), config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase());
			manager.connect();
			printMessage(Messages.getString("SUCCESSFULLY_CONNECTED_TO_", firstNode.getNodeIp()), display);

			printMessage(Messages.getString("STARTING_FIRST_NODE_AT_", firstNode.getNodeIp()), display);
			manager.execCommand("galera_new_cluster", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_STARTED_FIRST_NODE_AT_", firstNode.getNodeIp()),
					display);

			printMessage(
					Messages.getString("WAITING_FOR_A_FEW_SECONDS_UNTIL_MYSQL_UP_AT_", firstNode.getNodeIp()),
					display);
			Thread.sleep(30000);

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_NODE_", firstNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} catch (CommandExecutionException e) {
			printMessage(
					Messages.getString("EXCEPTION_RAISED_WHILE_STARTING_FIRST_NODE_AT_", firstNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}
	}

	private void configureNodeFromNode(DatabaseNodeInfoModel firstNode, DatabaseNodeInfoModel clusterNode,
			Display display) throws Exception {

		SSHManager manager = null;

		try {
			printMessage(Messages.getString("CONNECTING_TO_", firstNode.getNodeIp()), display);
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(),
					config.getDatabasePort(), config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase());
			manager.connect();
			printMessage(Messages.getString("SUCCESSFULLY_CONNECTED_TO_", firstNode.getNodeIp()), display);

			printMessage(Messages.getString("INSTALLING_SSHPASS_TO_", firstNode.getNodeIp()), display);
			manager.execCommand("apt-get -y --force-yes install sshpass", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_SSHPASS_TO_", firstNode.getNodeIp()), display);

			printMessage(Messages.getString("SENDING_DEBIAN_CNF_FROM_", firstNode.getNodeIp(), clusterNode.getNodeIp()), display);
			manager.execCommand(
					"sshpass -p \"{0}\" scp -o StrictHostKeyChecking=no /etc/mysql/debian.cnf root@{1}:/etc/mysql/",
					new Object[] { clusterNode.getNodeRootPwd(), clusterNode.getNodeIp() });
			printMessage(Messages.getString("SUCCESSFULLY_SENT_DEBIAN_CNF_FROM_", firstNode.getNodeIp(), clusterNode.getNodeIp()), display);

		} catch (SSHConnectionException e) {
			printMessage(firstNode.getNodeIp() + " " + Messages.getString("COULD_NOT_CONNECT_TO_NODE_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_",e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CONFIGURING_AND_STARTING_NODE_AT_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_",e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}
	}

	private void configureNodeFromLocal(DatabaseNodeInfoModel clusterNode, Display display) throws Exception {

		SSHManager manager = null;

		try {

			printMessage(Messages.getString("CONNECTING_TO_", clusterNode.getNodeIp()), display);
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getDatabasePort(), config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase());
			manager.connect();
			printMessage(Messages.getString("SUCCESSFULLY_CONNECTED_TO_", clusterNode.getNodeIp()), display);

			printMessage(Messages.getString("MODIFYING_DEBIAN_CNF_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("sed -i '/password/c\\password = 1' /etc/mysql/debian.cnf", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_MODIFIED_DEBIAN_CNF_AT_", clusterNode.getNodeIp()),
					display);

		} catch (SSHConnectionException e) {
			printMessage(clusterNode.getNodeIp() + " " + Messages.getString("COULD_NOT_CONNECT_TO_NODE_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_",e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} catch (CommandExecutionException e) {
			printMessage(
					Messages.getString("EXCEPTION_RAISED_WHILE_CONFIGURING_NODE_AT_", clusterNode.getNodeIp()),
					display);
			printMessage(Messages.getString("ERROR_MESSAGE_",e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}
	}

	private void startNode(DatabaseNodeInfoModel clusterNode, Display display) throws Exception {

		SSHManager manager = null;

		try {

			printMessage(Messages.getString("CONNECTING_TO_", clusterNode.getNodeIp()), display);
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getDatabasePort(), config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase());
			manager.connect();
			printMessage(Messages.getString("SUCCESSFULLY_CONNECTED_TO_", clusterNode.getNodeIp()), display);

			printMessage(Messages.getString("STARTING_NODE_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("service mysql start", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_STARTED_NODE_AT_", clusterNode.getNodeIp()), display);

		} catch (SSHConnectionException e) {
			printMessage(clusterNode.getNodeIp() + " " + Messages.getString("COULD_NOT_CONNECT_TO_NODE_", clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_STARTING_NODE_AT_", clusterNode.getNodeIp()),
					display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}
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
				txtLogConsole.setSelection(txtLogConsole.getCharCount() - 1);
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

	/**
	 * Clears log console by set its content to empty string.
	 */
	private void clearLogConsole(Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				txtLogConsole.setText("");
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
		return this.nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
