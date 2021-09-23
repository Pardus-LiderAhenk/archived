package tr.org.liderahenk.installer.lider.callables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class DatabaseSetupClusterNodeCallable implements Callable<Boolean> {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseSetupClusterNodeCallable.class);

	private String nodeIp;
	private String nodeRootPwd;
	private String nodeName;

	private Display display;
	private LiderSetupConfig config;
	private Text txtLogConsole;

	public DatabaseSetupClusterNodeCallable(String nodeIp, String nodeRootPwd, String nodeName, Display display,
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

		return setupClusterNode();
	}

	private boolean setupClusterNode() {

		SSHManager manager = null;

		boolean successfullSetup = false;
		try {
			// Check SSH connection
			try {
				printMessage(Messages.getString("CHECKING_CONNECTION_TO_", nodeIp), display);

				manager = new SSHManager(nodeIp, "root", nodeRootPwd, config.getDatabasePort(),
						config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase());
				manager.connect();

				printMessage(Messages.getString("CONNECTION_ESTABLISHED_TO_", nodeIp), display);
				logger.info("Connection established to: {} with username: {}", new Object[] { nodeIp, "root" });

			} catch (SSHConnectionException e) {
				printMessage(Messages.getString("COULD_NOT_CONNECT_TO_NODE_", nodeIp), display);
				printMessage(Messages.getString("CHECK_SSH_ROOT_PERMISSONS_OF_", nodeIp), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), nodeIp), display);
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
				printMessage(Messages.getString("COULD_NOT_UPDATE_PACKAGE_LIST_OF_", nodeIp), display);
				printMessage(Messages.getString("CHECK_INTERNET_CONNECTION_OF_", nodeIp), display);
				printMessage(Messages.getString("CHECK_REPOSITORY_LISTS_OF_", nodeIp), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), nodeIp), display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Install software-properties-common
			// Add keyserver
			// Add repository
			try {
				printMessage(Messages.getString("INSTALLING_PACKAGE_", "software-properties-common", nodeIp), display);
				manager.execCommand("apt-get -y --force-yes install software-properties-common", new Object[] {});
				printMessage(
						Messages.getString("SUCCESSFULLY_INSTALLED_PACKAGE_", "software-properties-common", nodeIp),
						display);

				printMessage(Messages.getString("ADDING_KEYSERVER_TO_", nodeIp), display);
				manager.execCommand("apt-key adv --recv-keys --keyserver keyserver.ubuntu.com 0xcbcb082a1bb943db",
						new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_ADDED_KEYSERVER_TO_", nodeIp), display);

				printMessage(Messages.getString("ADDING_REPOSITORY_",
						"'ftp://ftp.ulak.net.tr/pub/MariaDB/repo/10.1/debian jessie main'", nodeIp), display);
				manager.execCommand(
						"echo 'deb [arch=amd64,i386] ftp://ftp.ulak.net.tr/pub/MariaDB/repo/10.1/debian jessie main' > /etc/apt/sources.list.d/galera.list",
						new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_ADDED_REPOSITORY_",
						"'ftp://ftp.ulak.net.tr/pub/MariaDB/repo/10.1/debian jessie main'", nodeIp), display);

				printMessage(Messages.getString("UPDATING_PACKAGE_LIST_OF_", nodeIp), display);
				manager.execCommand("apt-get update", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_UPDATED_PACKAGE_LIST_OF_", nodeIp), display);
				logger.info("Successfully done prerequiste part at: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_DURING_PREREQUISITES_AT_", nodeIp), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE_AT", e.getMessage(), nodeIp), display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Set frontend as noninteractive
			// Set debconf values
			try {
				printMessage(Messages.getString("SETTING_DEBIAN_FRONTEND_AT_", nodeIp), display);
				manager.execCommand("export DEBIAN_FRONTEND='noninteractive'", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_SET_DEBIAN_FRONTEND_AT_", nodeIp), display);

				final String[] debconfValues = generateDebconfValues();

				printMessage(Messages.getString("SETTING_DEB_CONF_SELECTIONS_AT") + " " + nodeIp, display);
				for (String value : debconfValues) {
					manager.execCommand("debconf-set-selections <<< '{0}'", new Object[] { value });
				}
				printMessage(Messages.getString("SUCCESSFULLY_SET_DEB_CONF_SELECTIONS_AT") + " " + nodeIp, display);
				logger.info("Successfully done debconf selections part at: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_DURING_DEBCONF_AT") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Purge anything about mariadb and mysql
			try {
				printMessage(Messages.getString("CLEANING_BEFORE_INSTALLATION_AT") + " " + nodeIp, display);
				manager.execCommand("apt-get -y --force-yes purge -y mysql-* mariadb-*", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_CLEANED_BEFORE_INSTALLATION_AT") + " " + nodeIp, display);
				logger.info("Successfully successfully cleaned before installation at: {}", new Object[] { nodeIp });
			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("COULD_NOT_CLEAN_AT") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Install mariadb-server-10.1
			try {
				printMessage(Messages.getString("INSTALLING_PACKAGE") + " 'mariadb-server-10.1' to: " + nodeIp,
						display);
				manager.execCommand("apt-get -y --force-yes install mariadb-server-10.1", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_PACKAGE") + " 'software-properties-common' to: "
						+ nodeIp, display);
				logger.info("Successfully installed package mariadb-server-10.1 at: {}", new Object[] { nodeIp });
			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("COULD_NOT_INSTALL_MARIADB_TO") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Start mysql service
			// Execute mysql commands(first normal server commands)
			try {
				printMessage(Messages.getString("STARTING_MYSQL_SERVICE_AT") + " " + nodeIp, display);
				manager.execCommand("service mysql start", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_STARTED_MYSQL_SERVICE_AT") + nodeIp, display);

				printMessage(Messages.getString("EXECUTING_MYSQL_COMMANDS_AT") + nodeIp, display);
				manager.execCommand(
						"mysql -uroot -p{0} -e \"GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '{1}' WITH GRANT OPTION;\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseRootPassword() });
				manager.execCommand("mysql -uroot -p{0} -e \"DELETE FROM mysql.user WHERE user='';\"",
						new Object[] { config.getDatabaseRootPassword() });
				manager.execCommand("mysql -uroot -p{0} -e \"GRANT ALL ON *.* TO 'root'@'%' IDENTIFIED BY '{1}';\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseRootPassword() });
				manager.execCommand("mysql -uroot -p{0} -e \"GRANT USAGE ON *.* to {1}@'%' IDENTIFIED BY '{2}';\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseSstUsername(),
								config.getDatabaseSstPwd() });
				manager.execCommand("mysql -uroot -p{0} -e \"GRANT ALL PRIVILEGES on *.* to {1}@'%';\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseSstUsername() });
				manager.execCommand("mysql -uroot -p{0} -e \"FLUSH PRIVILEGES;\"",
						new Object[] { config.getDatabaseRootPassword() });
				printMessage(Messages.getString("CREATING_DATABASE_AT") + " " + nodeIp, display);
				manager.execCommand(
						"mysql -uroot -p{0} -e \"CREATE DATABASE liderdb DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci\"",
						new Object[] { config.getDatabaseRootPassword() });
				printMessage(Messages.getString("SUCCESSFULLY_EXECUTED_MYSQL_COMMANDS_AT") + nodeIp, display);
				logger.info("Successfully mysql commands at: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_ON_MYSQL_SERVICE_AT") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

			// Stop mysql service
			// Send galera.cnf
			try {
				printMessage(Messages.getString("STOPPING_MYSQL_SERVICE_AT_", nodeIp), display);
				manager.execCommand("service mysql stop", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_STOPPED_MYSQL_SERVICE_AT") + nodeIp, display);

				printMessage(Messages.getString("CREATING_CNF_FILE"), display);
				String galeraCnf = readFile("galera.cnf");
				Map<String, String> map = new HashMap<>();
				map.put("#CLUSTER_NAME", config.getDatabaseClusterName());
				map.put("#CLUSTER_ADDRESS", config.getDatabaseClusterAddress());
				map.put("#SST_USERNAME", config.getDatabaseSstUsername());
				map.put("#SST_PWD", config.getDatabaseSstPwd());
				map.put("#NODE_ADDRESS", nodeIp);
				map.put("#NODE_NAME", nodeName);

				galeraCnf = LiderAhenkUtils.replace(map, galeraCnf);

				File galeraCnfFile = LiderAhenkUtils.writeToFile(galeraCnf, "galera.cnf");
				printMessage(Messages.getString("SUCCESSFULLY_CREATED_CNF_FILE"), display);

				printMessage(Messages.getString("SENDING_CNF_FILE_TO_", nodeIp), display);
				manager.copyFileToRemote(galeraCnfFile, "/etc/mysql/conf.d/", false);
				printMessage(Messages.getString("SUCCESSFULLY_SENT_CNF_FILE_TO_", nodeIp), display);
				logger.info("Successfully sent galera.cnf to: {}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CONFIGURING_MYSQL_AT") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.error(e.getMessage(), e);
				throw new Exception();
			}

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
	 * Generates debconf values for database root password
	 * 
	 * @return
	 */
	public String[] generateDebconfValues() {
		String debconfPwd = PropertyReader.property("database.cluster.debconf.password") + " "
				+ config.getDatabaseRootPassword();
		String debconfPwdAgain = PropertyReader.property("database.cluster.debconf.password.again") + " "
				+ config.getDatabaseRootPassword();
		return new String[] { debconfPwd, debconfPwdAgain };
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
