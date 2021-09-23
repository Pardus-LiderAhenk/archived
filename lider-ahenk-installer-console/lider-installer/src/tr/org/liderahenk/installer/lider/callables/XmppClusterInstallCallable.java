package tr.org.liderahenk.installer.lider.callables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.model.XmppNodeInfoModel;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class XmppClusterInstallCallable implements Callable<Boolean> {

	private static final Logger logger = LoggerFactory.getLogger(XmppClusterInstallCallable.class);

	private String nodeIp;
	private String nodeRootPwd;
	private String nodeName;

	private Display display;
	private LiderSetupConfig config;
	private Text txtLogConsole;

	public XmppClusterInstallCallable(String nodeIp, String nodeRootPwd, String nodeName, Display display,
			LiderSetupConfig config, Text txtLogConsole) {
		super();
		this.nodeIp = nodeIp;
		this.nodeRootPwd = nodeRootPwd;
		this.nodeName = nodeName;
		this.display = display;
		this.config = config;
		this.txtLogConsole = txtLogConsole;
	}

	@Override
	public Boolean call() throws Exception {

		return installStandAloneEjabberd();
	}

	private Boolean installStandAloneEjabberd() {

		SSHManager manager = null;

		boolean successfullSetup = false;
		try {
			// Check SSH connection
			try {
				printMessage(Messages.getString("CHECKING_CONNECTION_TO_", nodeIp), display);

				manager = new SSHManager(nodeIp, "root", nodeRootPwd, config.getXmppPort(),
						config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
				manager.connect();

				printMessage(Messages.getString("CONNECTION_ESTABLISHED_TO") + " " + nodeIp, display);
				logger.info("Connection established to: {} with username: {}",
						new Object[] { nodeIp, "root" });

			} catch (SSHConnectionException e) {
				printMessage(Messages.getString("COULD_NOT_CONNECT_TO_NODE") + " " + nodeIp, display);
				printMessage(Messages.getString("CHECK_SSH_ROOT_PERMISSONS_OF" + " " + nodeIp), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Update package list
			try {
				printMessage(Messages.getString("UPDATING_PACKAGE_LIST_OF_", nodeIp), display);
				manager.execCommand("apt-get update", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_UPDATED_PACKAGE_LIST_OF_", nodeIp), display);
				logger.info("Successfully updated package list of {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("COULD_NOT_UPDATE_PACKAGE_LIST_OF") + " " + nodeIp, display);
				printMessage(Messages.getString("CHECK_INTERNET_CONNECTION_OF") + " " + nodeIp, display);
				printMessage(Messages.getString("CHECK_REPOSITORY_LISTS_OF") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Send deb file of Ejabberd
			try {

				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("ejabberd_16.06-0_amd64.deb");
				File ejabberdDeb = LiderAhenkUtils.streamToFile(inputStream, "ejabberd_16.06-0_amd64.deb");

				printMessage(Messages.getString("SENDING_DEB_FILE_TO") + " " + nodeIp, display);
				manager.copyFileToRemote(ejabberdDeb, "/tmp/", false);
				printMessage(Messages.getString("SUCCESSFULLY_SENT_DEB_FILE_TO") + " " + nodeIp, display);
				logger.info("Successfully sent Ejabberd deb to: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_DEB_FILE_TO") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Install Gdebi
			try {
				printMessage(Messages.getString("INSTALLING_GDEBI_TO_", nodeIp), display);
				manager.execCommand("apt-get install -y --force-yes gdebi", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_GDEBI_TO_", nodeIp), display);
				logger.info("Successfully installed gdebi to: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_INSTALLING_GDEBI_TO_", nodeIp), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}
			
			// Install Ejabberd package
			try {
				printMessage(Messages.getString("INSTALLING_EJABBERD_TO") + " " + nodeIp, display);
				manager.execCommand("gdebi -n /tmp/ejabberd_16.06-0_amd64.deb", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_EJABBERD_TO") + " " + nodeIp, display);
				logger.info("Successfully installed Ejabberd at: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_INSTALLING_EJABBERD_TO") + " " + nodeIp,
						display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Send ejabberd.yml
			try {

				printMessage(Messages.getString("CREATING_YML_FILE"), display);
				String ejabberdYml = readFile("ejabberd_cluster.yml");
				Map<String, String> map = new HashMap<>();
				map.put("#SERVICE_NAME", config.getXmppHostname());
				map.put("#LDAP_SERVER", config.getXmppLdapServerAddress());
				map.put("#LDAP_ROOT_DN", config.getXmppLdapRootDn());
				map.put("#LDAP_ROOT_PWD", config.getXmppLdapRootPwd());
				map.put("#LDAP_BASE_DN", config.getXmppLdapBaseDn());

				ejabberdYml = LiderAhenkUtils.replace(map, ejabberdYml);
				File ejabberdYmlFile = LiderAhenkUtils.writeToFile(ejabberdYml, "ejabberd.yml");
				printMessage(Messages.getString("SUCCESSFULLY_CREATED_YML_FILE"), display);

				printMessage(Messages.getString("SENDING_EJABBERD_YML_TO") + " " + nodeIp, display);
				manager.copyFileToRemote(ejabberdYmlFile, "/opt/ejabberd-16.06/conf/", false);
				printMessage(Messages.getString("SUCCESSFULLY_SENT_EJABBERD_YML_TO") + " " + nodeIp, display);
				logger.info("Successfully sent ejabberd.yml to: {}", new Object[] { nodeIp });
			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_EJABBERD_YML_TO") + " " + nodeIp,
						display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Modify ejabberdctl.cfg
			try {
				printMessage(Messages.getString("MODIFYING_EJABBERD_CTL_CFG_AT_", nodeIp), display);
				manager.execCommand(
						"sed -i '/#ERLANG_NODE/c\\ERLANG_NODE=ejabberd@{0}.{1}' /opt/ejabberd-16.06/conf/ejabberdctl.cfg",
						new Object[] { nodeName, config.getXmppHostname() });
				printMessage(Messages.getString("SUCCESSFULLY_MODIFIED_EJABBERD_CTL_CFG_AT_", nodeIp), display);
				logger.info("Successfully modified ejabberdctl.cfg at: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_MODIFYING_EJABBERDCTL_AT") + " " + nodeIp,
						display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Modify /etc/hosts
			try {
				printMessage(Messages.getString("MODIFYING_ETC_HOSTS_AT_", nodeIp), display);

				// Write each node to /etc/hosts
				for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap().entrySet()
						.iterator(); iterator.hasNext();) {

					Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
					final XmppNodeInfoModel clusterNode = entry.getValue();

					manager.execCommand("sed -i '1 i\\{0} {1}.{2}' /etc/hosts", new Object[] { clusterNode.getNodeIp(),
							clusterNode.getNodeName(), config.getXmppHostname() });
				}

				printMessage(Messages.getString("SUCCESSFULLY_MODIFIED_ETC_HOSTS_AT") + " " + nodeIp, display);
				logger.info("Successfully modified /etc/hosts at: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_MODIFYING_ETC_HOSTS_AT") + " " + nodeIp,
						display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Create admin user with post install script of Ejabberd
			// try {
			// printMessage(Messages.getString("CREATING_ADMIN_USER_AT") + " " +
			// nodeIp, display);
			// manager.execCommand("/opt/ejabberd-16.06/bin/postinstall.sh admin
			// {0} {1}",
			// new Object[] { config.getXmppHostname(), config.getXmppAdminPwd()
			// });
			// printMessage(Messages.getString("SUCCESSFULLY_CREATED_ADMIN_USER_AT")
			// + " " + nodeIp, display);
			// logger.log(Level.INFO, "Successfully created admin user at: {0}",
			// new Object[] { nodeIp });
			//
			// } catch (CommandExecutionException e) {
			// printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CREATING_ADMIN_USER_AT")
			// + " " + nodeIp, display);
			// printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " +
			// e.getMessage() + " at " + nodeIp, display);
			// logger.log(Level.SEVERE, e.getMessage());
			// e.printStackTrace();
			// throw new Exception();
			// }

			printMessage(Messages.getString("INSTALLATION_COMPLETED_SUCCESSFULLY_AT") + " " + nodeIp, display);
			successfullSetup = true;

		} catch (Exception e) {
			printMessage(Messages.getString("INSTALLATION_FAILED_AT") + " " + nodeIp, display);
			e.printStackTrace();
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}

		return successfullSetup;
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

}
