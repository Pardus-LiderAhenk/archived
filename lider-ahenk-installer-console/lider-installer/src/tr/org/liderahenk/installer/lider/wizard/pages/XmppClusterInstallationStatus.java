package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import tr.org.liderahenk.installer.lider.callables.XmppClusterInstallCallable;
import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.utils.PageFlowHelper;
import tr.org.liderahenk.installer.lider.wizard.model.XmppNodeInfoModel;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class XmppClusterInstallationStatus extends WizardPage
		implements IXmppPage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;
	boolean canGoBack = false;

	private final static String CLUSTER_CLIENTS = "server  server1 #NODE_IP:5222 check fall 3 id #CLIENT_ID inter 5000 rise 3 slowstart 120000 weight 50";
	private final static String CLUSTER_CLIENTS_SSL = "server  server1 #NODE_IP:5223 check fall 3 id #CLIENT_SSL_ID inter 5000 rise 3 slowstart 240000 weight 50";
	private final static String CLUSTER_SERVERS = "server  server1 #NODE_IP:5269 check fall 3 id #SERVER_ID inter 5000 rise 3 slowstart 60000 weight 50";

	private Integer clientId = 1005;
	private Integer clientSslId = 1008;
	private Integer serverId = 10011;

	private static final String EJABBERD_SRG_CREATE = "{0}ejabberdctl srg-create everyone {1} \"everyone\" this_is_everyone everyone";
	private static final String EJABBERD_SRG_ADD_ALL = "{0}ejabberdctl srg-user-add @all@ {1} everyone {2}";
	private static final String EJABBERD_REGISTER = "{0}ejabberdctl register {1} {2} {3}";

	private String erlangCookie = null;

	private static final Logger logger = LoggerFactory.getLogger(XmppClusterInstallationStatus.class);

	private List<XmppNodeInfoModel> installedNodeList;
	private List<XmppNodeInfoModel> newNodeList;

	public XmppClusterInstallationStatus(LiderSetupConfig config) {
		super(XmppClusterInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("4.4 " + Messages.getString("XMPP_CLUSTER_INSTALLATION"));
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

		// Start Ejabberd installation here.
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

					// A node that will be the first to start in cluster
					XmppNodeInfoModel firstNode = null;

					// Selected already installed nodes and iterate over them
					selectInstalledAndNewNodes();

					boolean allNodesSuccess = false;

					for (XmppNodeInfoModel clusterNode : installedNodeList) {
						try {
							// If first node is not selected, select it
							if (firstNode == null) {
								firstNode = clusterNode;
							}

							// Read .erlang.cookie just once from first node.
							if (erlangCookie == null) {
								readErlangCookie(firstNode, display);
							}

							// Configure already installed node
							onlyConfigureNode(clusterNode, display);

							allNodesSuccess = true;

						} catch (Exception e) {
							allNodesSuccess = false;
							printMessage(
									Messages.getString(
											"EXCEPTION_RAISED_WHILE_CONFIGURING_ONE_OF_ALREADY_INSTALLED_NODES"),
									display);
							printMessage(
									Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()),
									display);
							logger.error(e.getMessage(), e);
						}

					}

					for (XmppNodeInfoModel clusterNode : newNodeList) {
						Callable<Boolean> callable = new XmppClusterInstallCallable(clusterNode.getNodeIp(),
								clusterNode.getNodeRootPwd(), clusterNode.getNodeName(), display, config,
								txtLogConsole);
						Future<Boolean> result = executor.submit(callable);
						resultList.add(result);
					}

					try {
						executor.shutdown();
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					allNodesSuccess = false;

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
							// If first node is not selected from only
							// configured nodes, get first node
							if (firstNode == null) {
								firstNode = config.getXmppNodeInfoMap().get(1);
							}

							if (config.getXmppAccessKeyPath() == null) {
								// Install sshpass to first node
								installSshPass(firstNode, display);

								for (XmppNodeInfoModel clusterNode : newNodeList) {
									if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
										// Send Erlang cookie from first node to
										// others
										sendErlangCookie(firstNode, clusterNode, display);
									}
								}
							} else {

								readErlangCookie(firstNode, display);

								for (XmppNodeInfoModel clusterNode : newNodeList) {
									if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
										modifyErlangCookie(clusterNode, display);
									}
								}
							}

							// Start Ejabberd at each node
							for (XmppNodeInfoModel clusterNode : newNodeList) {
								defineService(clusterNode, display);

								startEjabberd(clusterNode, display);
							}
							printMessage(Messages.getString("WAITING_EJABBERD_TO_START"), display);
							Thread.sleep(20000);

							// Restart Ejabberd at each node
							for (XmppNodeInfoModel clusterNode : newNodeList) {
								restartEjabberd(clusterNode, display);
							}
							printMessage(Messages.getString("WAITING_EJABBERD_TO_RESTART"), display);
							Thread.sleep(20000);

							// Join each node except first node to cluster
							for (XmppNodeInfoModel clusterNode : newNodeList) {
								if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
									// start other nodes.
									joinToCluster(firstNode.getNodeName(), clusterNode, display);
								}
							}

							installHaProxy(config.getXmppProxyAddress(), config.getXmppProxyPwd(),
									config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(),
									config.getXmppNodeInfoMap(), display);

							// Restart Ejabberd at first node.
							// Acutally it should not be necessary
							// but there may be a bug about that.
							// Because if first node is not restarted after
							// join_cluster commands, its ejabberdctl script
							// does not work properly
							restartEjabberd(firstNode, display);

							if (installedNodeList == null || installedNodeList.isEmpty()) {
								// Create shared roster group and users
								createSrgAndUsers(firstNode, display);
							}

							canGoBack = false;

							isInstallationFinished = true;

							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									progressBar.setVisible(false);
								}
							});

							printMessage(Messages.getString("EJABBERD_CLUSTER_INSTALLATION_FINISHED"), display);

							config.setInstallationFinished(isInstallationFinished);

							setPageCompleteAsync(isInstallationFinished, display);

						} catch (Exception e) {
							e.printStackTrace();
							printMessage(Messages.getString("ERROR_OCCURED_WHILE_STARTING_OR_CONFIGURING_NODE"),
									display);
							printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
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

					} else

					{
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
								txtLogConsole
										.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
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

	private void onlyConfigureNode(XmppNodeInfoModel clusterNode, Display display) throws Exception {

		SSHManager manager = null;

		// Check SSH connection
		try {
			printMessage(Messages.getString("CHECKING_CONNECTION_TO_", clusterNode.getNodeIp()), display);

			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("CONNECTION_ESTABLISHED_TO_", clusterNode.getNodeIp()), display);
			logger.info("Connection established to: {} with username: {}",
					new Object[] { clusterNode.getNodeIp(), "root" });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_NODE_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("CHECK_SSH_ROOT_PERMISSONS_OF_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

		// Modify /etc/hosts
		try {
			printMessage(Messages.getString("CONFIGURING_ALREADY_INSTALLED_NODE_AT_", clusterNode.getNodeIp()),
					display);

			// Write each node to /etc/hosts
			for (XmppNodeInfoModel newNode : newNodeList) {
				manager.execCommand("sed -i '1 i\\{0} {1}.{2}' /etc/hosts",
						new Object[] { newNode.getNodeIp(), newNode.getNodeName(), config.getXmppHostname() });
			}

			printMessage(Messages.getString("SUCCESSFULLY_MODIFIED_ETC_HOSTS_AT_", clusterNode.getNodeIp()), display);
			logger.info("Successfully modified /etc/hosts at: {}", new Object[] { clusterNode.getNodeIp() });

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CONFIGURING_ALREADY_INSTALLED_NODE_AT",
					clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}
	}

	private void selectInstalledAndNewNodes() {
		installedNodeList = new ArrayList<XmppNodeInfoModel>();
		newNodeList = new ArrayList<XmppNodeInfoModel>();

		for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap().entrySet()
				.iterator(); iterator.hasNext();) {

			Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
			final XmppNodeInfoModel clusterNode = entry.getValue();

			if (!clusterNode.isNodeNewSetup()) {
				installedNodeList.add(clusterNode);
			} else {
				newNodeList.add(clusterNode);
			}
		}
	}

	private void defineService(XmppNodeInfoModel clusterNode, Display display) throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("DEFINING_EJABBERD_AS_SERVICE_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("ln -fs " + PropertyReader.property("xmpp.bin.path")
					+ "ejabberd.init /etc/init.d/ejabberd && update-rc.d ejabberd defaults", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_DEFINED_EJABBERD_AS_SERVICE_AT_", clusterNode.getNodeIp()),
					display);

			logger.info("Successfully defined service at {}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_DEFINING_SERVICE_AT_", clusterNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

	}

	private void modifyErlangCookie(XmppNodeInfoModel clusterNode, Display display) throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("MODIFYING_ERLANG_COOKIE_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("sed -i '1s/.*/{0}/' {1}.erlang.cookie",
					new Object[] { erlangCookie, PropertyReader.property("xmpp.cluster.path") });
			printMessage(Messages.getString("SUCCESSFULLY_MODIFIED_ERLANG_COOKIE_AT_", clusterNode.getNodeIp()),
					display);

			logger.info("Successfully modified .erlang.cookie at {}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(
					Messages.getString("EXCEPTION_RAISED_WHILE_MODIFYING_ERLANG_COOKIE_AT_", clusterNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

	}

	private void readErlangCookie(XmppNodeInfoModel firstNode, Display display) throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("READING_ERLANG_COOKIE_FROM_", firstNode.getNodeIp()), display);
			erlangCookie = manager.execCommand("more {0}.erlang.cookie",
					new Object[] { PropertyReader.property("xmpp.cluster.path") });
			// Remove new lines
			erlangCookie = erlangCookie.replaceAll("\n", "");
			printMessage(Messages.getString("SUCCESSFULLY_READ_ERLANG_COOKIE_FROM_", firstNode.getNodeIp()), display);

			logger.info("Successfully read .erlang.cookie from {}", new Object[] { firstNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", firstNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_READING_ERLANG_COOKIE_AT_") + firstNode.getNodeIp(),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), firstNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}
	}

	private void createSrgAndUsers(XmppNodeInfoModel firstNode, Display display) throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("CREATING_SHARED_ROSTER_GROUP_AT_", firstNode.getNodeIp()), display);
			manager.execCommand(EJABBERD_SRG_CREATE,
					new Object[] { PropertyReader.property("xmpp.cluster.bin.path"), config.getXmppHostname() });
			printMessage(
					Messages.getString("SUCCESSFULLY_CREATED_SHARED_ROSTER_GROUP_AT") + " " + firstNode.getNodeIp(),
					display);

			// TODO check with "srg_get_info everyone SERVICE_NAME".
			// TODO if not created try again.

			printMessage(Messages.getString("ADDING_DEFAULT_SRG_BEHAVIOUR_AT_", firstNode.getNodeIp()), display);
			manager.execCommand(EJABBERD_SRG_ADD_ALL, new Object[] { PropertyReader.property("xmpp.cluster.bin.path"),
					config.getXmppHostname(), config.getXmppHostname() });
			printMessage(Messages.getString("SUCCESSFULLY_ADDED_DEFAULT_SRG_BEHAVIOUR_AT_", firstNode.getNodeIp()),
					display);
			logger.info("Successfully created shared roster group at {}", new Object[] { firstNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", firstNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CREATING_SRG_AT_", firstNode.getNodeIp()), display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), firstNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

		try {
			printMessage(Messages.getString("REGISTERING_ADMIN_USER_AT_", firstNode.getNodeIp()), display);
			manager.execCommand(EJABBERD_REGISTER, new Object[] { PropertyReader.property("xmpp.cluster.bin.path"),
					"admin", config.getXmppHostname(), config.getXmppAdminPwd() });
			printMessage(Messages.getString("SUCCESSFULLY_REGISTERED_ADMIN_USER_AT_", firstNode.getNodeIp()), display);

			printMessage(
					Messages.getString("REGISTERING_USER_AT_", config.getXmppLiderUsername(), firstNode.getNodeIp()),
					display);
			manager.execCommand(EJABBERD_REGISTER, new Object[] { PropertyReader.property("xmpp.cluster.bin.path"),
					config.getXmppLiderUsername(), config.getXmppHostname(), config.getXmppLiderPassword() });
			printMessage(Messages.getString("SUCCESSFULLY_REGISTERED_USER_AT_", config.getXmppLiderUsername(),
					firstNode.getNodeIp()), display);

			logger.info("Successfully registered users at {}", new Object[] { firstNode.getNodeIp() });

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_REGISTERING_USERS_AT_", firstNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), firstNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

	}

	private void installHaProxy(String xmppProxyAddress, String xmppProxyPwd, String xmppAccessKeyPath,
			String xmppAccessPassphrase, Map<Integer, XmppNodeInfoModel> xmppNodeInfoMap, Display display)
			throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(xmppProxyAddress, "root", xmppProxyPwd, config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("INSTALLING_HAPROXY_PACKAGE_TO", xmppProxyAddress), display);
			manager.execCommand("apt-get -y --force-yes install haproxy", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_HAPROXY_PACKAGE_TO", xmppProxyAddress), display);
			logger.info("Successfully installed HaProxy to {}", new Object[] { xmppProxyAddress });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", xmppProxyAddress), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_INSTALLING_HAPROXY_PACKAGE_AT", xmppProxyAddress),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), xmppProxyAddress), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

		printMessage(Messages.getString("PREPARING_BACKEND_PROPERTIES"), display);
		Map<String, String> propertyMap = prepareBackendProperties();
		printMessage(Messages.getString("SUCCESSFULLY_PREPARED_BACKEND_PROPERTIES"), display);

		printMessage(Messages.getString("CREATING_HAPROXY_CONFIG_FILE"), display);
		String haproxyCfg = readFile("haproxy_ejabberd.cfg");

		Map<String, String> map = new HashMap<>();
		map.put("#HAPROXY_ADDRESS", config.getXmppProxyAddress());
		map.put("#CLUSTER_CLIENTS", propertyMap.get("CLUSTER_CLIENTS"));
		map.put("#CLUSTER_CLIENTS_SSL", propertyMap.get("CLUSTER_CLIENTS_SSL"));
		map.put("#CLUSTER_SERVERS", propertyMap.get("CLUSTER_SERVERS"));

		haproxyCfg = LiderAhenkUtils.replace(map, haproxyCfg);
		File haproxyCfgFile = LiderAhenkUtils.writeToFile(haproxyCfg, "haproxy.cfg");
		printMessage(Messages.getString("SUCCESSFULLY_CREATED_HAPROXY_CONFIG_FILE"), display);
		logger.info("Successfully created haproxy.cfg");

		try {
			printMessage(Messages.getString("SENDING_HAPROXY_CONFIG_FILE_TO") + " " + xmppProxyAddress, display);
			manager.copyFileToRemote(haproxyCfgFile, "/etc/haproxy/", false);
			printMessage(Messages.getString("SUCCESSFULLY_SENT_HAPROXY_CONFIG_FILE_TO", xmppProxyAddress), display);
			logger.info("Successfully sent haproxy.cfg to {}", new Object[] { xmppProxyAddress });

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_HAPROXY_CFG_TO", xmppProxyAddress),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), xmppProxyAddress), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

		try {
			printMessage(Messages.getString("RESTARTING_HAPROXY_SERVICE_AT", xmppProxyAddress), display);
			manager.execCommand("service haproxy restart", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_RESTARTED_HAPROXY_SERVICE_AT", xmppProxyAddress), display);
			logger.info("Successfully restarted haproxy service at {}", new Object[] { xmppProxyAddress });

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_RESTARTING_HAPROXY_SERVICE_AT", xmppProxyAddress),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), xmppProxyAddress), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

		printMessage(Messages.getString("SUCCESSFULLY_COMPLETED_INSTALLATION_OF_HAPROXY_AT", xmppProxyAddress),
				display);
		logger.info("Successfully completed installation of HaProxy at: {}", new Object[] { xmppProxyAddress });
	}

	private Map<String, String> prepareBackendProperties() {

		Map<String, String> propertyMap = new HashMap<String, String>();

		String clusterClients = "";
		String clusterClientsSsl = "";
		String clusterServers = "";

		for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap().entrySet()
				.iterator(); iterator.hasNext();) {

			Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
			final XmppNodeInfoModel clusterNode = entry.getValue();

			clusterClients += CLUSTER_CLIENTS.replace("#NODE_IP", clusterNode.getNodeIp()).replace("#CLIENT_ID",
					clientId.toString());
			clusterClients += "\n\t";
			++clientId;
			clusterClientsSsl += CLUSTER_CLIENTS_SSL.replace("#NODE_IP", clusterNode.getNodeIp())
					.replace("#CLIENT_SSL_ID", clientSslId.toString());
			clusterClientsSsl += "\n\t";
			++clientSslId;
			clusterServers += CLUSTER_SERVERS.replace("#NODE_IP", clusterNode.getNodeIp()).replace("#SERVER_ID",
					serverId.toString());
			clusterServers += "\n\t";
			++serverId;
		}

		propertyMap.put("CLUSTER_CLIENTS", clusterClients);
		propertyMap.put("CLUSTER_CLIENTS_SSL", clusterClientsSsl);
		propertyMap.put("CLUSTER_SERVERS", clusterServers);

		return propertyMap;
	}

	private void joinToCluster(String firstNodeName, XmppNodeInfoModel clusterNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("JOINING_TO_CLUSTER_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl join_cluster 'ejabberd@{0}.{1}'",
					new Object[] { firstNodeName, config.getXmppHostname() });
			printMessage(Messages.getString("SUCCESSFULLY_JOINED_TO_CLUSTER_AT_", clusterNode.getNodeIp()), display);
			logger.info("Successfully joined to cluster at {}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_JOINING_TO_CLUSTER_AT_", clusterNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);

			boolean joinSuccessfull = false;
			for (int i = 0; i < 3; i++) {
				try {
					printMessage(Messages.getString("NODE_COULD_NOT_JOIN_TO_CLUSTER_AT_", clusterNode.getNodeIp()),
							display);
					printMessage(Messages.getString("WILL_RETRY_TO_JOIN_AT_", clusterNode.getNodeIp()), display);

					printMessage(Messages.getString("STOPPING_EJABBERD_AT_", clusterNode.getNodeIp()), display);
					manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl stop", new Object[] {});
					printMessage(Messages.getString("SUCCESSFULLY_STOPPED_EJABBERD_AT_", clusterNode.getNodeIp()),
							display);

					printMessage(Messages.getString("STARTING_EJABBERD_AT_", clusterNode.getNodeIp()), display);
					manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl start", new Object[] {});
					printMessage(Messages.getString("SUCCESSFULLY_STARTED_EJABBERD_AT_", clusterNode.getNodeIp()),
							display);

					printMessage(Messages.getString("WAITING_FOR_EJABBERD_TO_STARTUP_AT_", clusterNode.getNodeIp()),
							display);
					Thread.sleep(20000);

					printMessage(Messages.getString("RETRYING_TO_JOIN_TO_CLUSTER_AT_", clusterNode.getNodeIp()),
							display);
					manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl join_cluster 'ejabberd@{0}.{1}'",
							new Object[] { firstNodeName, config.getXmppHostname() });
					printMessage(Messages.getString("SUCCESSFULLY_JOINED_TO_CLUSTER_AT_", clusterNode.getNodeIp()),
							display);
					joinSuccessfull = true;
				} catch (CommandExecutionException e2) {
					joinSuccessfull = false;
					printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_REJOINING_TO_CLUSTER_AT_",
							clusterNode.getNodeIp()), display);
					printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()),
							display);
					printMessage(Messages.getString("WILL_RETRY_TO_JOIN_AT_", clusterNode.getNodeIp()), display);
					logger.error(e.getMessage(), e);
				}

				if (joinSuccessfull) {
					break;
				}
			}

			if (joinSuccessfull) {
				printMessage(Messages.getString("REJOINING_TO_CLUSTER_WAS_SUCCESSFULL_AT_", clusterNode.getNodeIp()),
						display);
			} else {
				printMessage(Messages.getString("REJOINING_TO_CLUSTER_FAILED_AT_", clusterNode.getNodeIp()), display);
				throw new Exception();
			}
		}
	}

	private void restartEjabberd(XmppNodeInfoModel clusterNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("RESTARTING_EJABBERD_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl restart", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_RESTARTED_EJABBERD_AT_", clusterNode.getNodeIp()), display);

			printMessage(Messages.getString("WAITING_FOR_RESTARTING_EJABBERD_AT_", clusterNode.getNodeIp()), display);
			Thread.sleep(15000);

			logger.info("Successfully restarted Ejabberd at {}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_RESTARTING_EJABBERD_AT_", clusterNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

	}

	private void startEjabberd(XmppNodeInfoModel clusterNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("STARTING_EJABBERD_AT_", clusterNode.getNodeIp()), display);
			manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl start", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_STARTED_EJABBERD_AT_", clusterNode.getNodeIp()), display);
			logger.info("Successfully started Ejabberd at {}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_STARTING_EJABBERD_AT_", clusterNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), clusterNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}

	}

	private void installSshPass(XmppNodeInfoModel firstNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("INSTALLING_SSHPASS_PACKAGE_TO") + " " + firstNode.getNodeIp(), display);
			manager.execCommand("apt-get -y --force-yes install sshpass",
					new Object[] { firstNode.getNodeRootPwd(), firstNode.getNodeIp() });
			printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_SSHPASS_PACKAGE_TO") + " " + firstNode.getNodeIp(),
					display);
			logger.info("Successfully installed sshpass to {}", new Object[] { firstNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO", firstNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_INSTALLING_SSHPASS_AT_", firstNode.getNodeIp()),
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), firstNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
		}
	}

	private void sendErlangCookie(XmppNodeInfoModel firstNode, XmppNodeInfoModel clusterNode, Display display)
			throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("SENDING_ERLANG_COOKIE_FROM_TO_", firstNode.getNodeIp(),
					clusterNode.getNodeIp()), display);
			manager.execCommand(
					"sshpass -p \"{0}\" scp -o StrictHostKeyChecking=no /opt/ejabberd-16.06/.erlang.cookie root@{1}:/opt/ejabberd-16.06/",
					new Object[] { clusterNode.getNodeRootPwd(), clusterNode.getNodeIp() });
			printMessage(Messages.getString("SUCCESSFULLY_SENT_ERLANG_COOKIE_FROM_TO_", firstNode.getNodeIp(),
					clusterNode.getNodeIp()), display);
			logger.info("Successfully sent Erlang cookie from {} to {}",
					new Object[] { firstNode.getNodeIp(), clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO_", clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("ERROR_MESSAGE_", e.getMessage()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_ERLANG_COOKIE_FROM_TO_",
					firstNode.getNodeIp(), clusterNode.getNodeIp()), display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), firstNode.getNodeIp()), display);
			logger.error(e.getMessage(), e);
			throw new Exception();
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
				txtLogConsole.setTopIndex(txtLogConsole.getLineCount() - 1);
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

	/**
	 * Reads file from classpath location of current project
	 * 
	 * @param fileName
	 */
	private String readFile(String fileName) {

		BufferedReader br = null;
		InputStream inputStream = null;

		String readingText = "";

		try {
			String currentLine;

			inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

			br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			while ((currentLine = br.readLine()) != null) {
				// Platform independent line separator.
				readingText += currentLine + System.getProperty("line.separator");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return readingText;
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
