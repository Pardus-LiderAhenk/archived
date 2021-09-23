package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.pardus.mys.liderahenksetup.constants.PackageInstaller;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;

/**
 * Utility class which provides common command execution methods (such as
 * installing/un-installing a package, checking version of a package etc.)
 * locally or remotely
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class SetupUtils {

	private static final Logger logger = LoggerFactory.getLogger(SetupUtils.class);

	/**
	 * Install package via apt-get
	 */
	private static final String INSTALL_PACKAGE_FROM_REPO_CMD = "apt-get install -y --force-yes {0}={1}";

	/**
	 * Install package via apt-get (without version)
	 */
	private static final String INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION = "apt-get install -y --force-yes {0}";

	/**
	 * Install given package via dpkg
	 */
	private static final String INSTALL_PACKAGE = "dpkg -i {0}";

	/**
	 * Uninstall package via apt-get
	 */
	private static final String UNINSTALL_PACKAGE_CMD = "apt-get remove --purge -y {0}";

	/**
	 * Add new repository
	 */
	private static final String ADD_APP_REPO_CMD = "add-apt-repository -y {0} && apt-get update";

	/**
	 * Turns off "frontend" (prompts) during installation
	 */
	private static final String SET_DEBIAN_FRONTEND = "export DEBIAN_FRONTEND='noninteractive'";

	/**
	 * Sets default values which used during the noninteractive installation
	 */
	private static final String DEBCONF_SET_SELECTIONS = "debconf-set-selections <<< '{0}'";

	/**
	 * Download file with its default file name on the server from provided URL.
	 * Downloaded file will be in /tmp/{0} folder.
	 */
	private static final String DOWNLOAD_PACKAGE = "wget ‐‐directory-prefix=/tmp/ {0}";

	/**
	 * Update package list before installing anything
	 */
	private static final String UPDATE_PACKAGE_LIST = "apt-get update";

	/**
	 * DowNload file with provided file name from provided URL. Downloaded file
	 * will be in /tmp/{0} folder.
	 */
	private static final String DOWNLOAD_PACKAGE_WITH_FILENAME = "wget --output-document=/tmp/{0} {1}";

	private static final String INSTALL_PACKAGE_GDEBI = "gdebi -n {0}";

	private static final String INSTALL_GDEBI = "apt-get install -y gdebi";

	/**
	 * Tries to connect via SSH. It uses username-password pair to connect.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param passphrase
	 * @return
	 */
	public static boolean canConnectViaSsh(final String ip, final String username, final String password,
			final String passphrase) {
		return canConnectViaSsh(ip, username, password, null, null, passphrase);
	}

	/**
	 * Tries to connect via SSH key. It uses SSH private key to connect.
	 * 
	 * @param ip
	 * @param username
	 *            default value is 'root'
	 * @param privateKey
	 * @return true if an SSH connection can be established successfully, false
	 *         otherwise
	 */
	public static boolean canConnectViaSshWithoutPassword(final String ip, final String username,
			final String privateKey, final String passphrase) {
		return canConnectViaSsh(ip, username == null ? "root" : username, null, null, privateKey, passphrase);
	}

	/**
	 * Tries to connect via SSH. If password parameter is null, then it tries to
	 * connect via SSH key
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @return true if an SSH connection can be established successfully, false
	 *         otherwise
	 */
	public static boolean canConnectViaSsh(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase) {
		SSHManager manager = null;
		try {
			manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
			manager.connect();
			logger.info("Connection established to: {} with username: {}",
					new Object[] { ip, username == null ? "root" : username });
			return true;
		} catch (SSHConnectionException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}
		return false;
	}

	/**
	 * Installs a package which specified by package name and version. Before
	 * calling this method, package existence should be ensured by calling
	 * packageExists() method.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param packageName
	 * @param version
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installPackage(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String packageName, final String version)
			throws SSHConnectionException, CommandExecutionException {
		logger.info("Installing package remotely on: {} with username: {}", new Object[] { ip, username });

		// Update package list first!
		SetupUtils.executeCommand(ip, username, password, port, privateKey, passphrase, UPDATE_PACKAGE_LIST);

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		// If version is not given
		if (version == null || "".equals(version)) {
			manager.execCommand(INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION, new Object[] { packageName });
			logger.info("Package {} installed successfully", new Object[] { packageName });
		} else {
			manager.execCommand(INSTALL_PACKAGE_FROM_REPO_CMD, new Object[] { packageName, version });
			logger.info("Package {}:{} installed successfully", new Object[] { packageName, version });
		}
		manager.disconnect();
	}

	/**
	 * Installs a package 'non-interactively' which specified by package name
	 * and version. Before installing the package it uses debconf-set-selections
	 * to insert default values which asked during the interactive installation.
	 * 
	 * Before calling this method, package existence should be ensured by
	 * calling packageExists() method.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param packageName
	 * @param passphrase
	 * @param version
	 * @param debconfValues
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void installPackageNoninteractively(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String packageName,
			final String version, final String[] debconfValues) throws Exception {

		// Update package list first!
		SetupUtils.executeCommand(ip, username, password, port, privateKey, passphrase, UPDATE_PACKAGE_LIST);

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		// Set frontend as noninteractive
		manager.execCommand(SET_DEBIAN_FRONTEND, new Object[] {});

		manager.disconnect();
		manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
		manager.connect();

		// Set debconf values
		for (String value : debconfValues) {
			manager.execCommand(DEBCONF_SET_SELECTIONS, new Object[] { value });
		}

		manager.disconnect();

		// Finally, install the package
		SetupUtils.installPackage(ip, username, password, port, privateKey, passphrase, packageName, version);
	}

	/**
	 * Installs a deb package file. This can be used when a specified deb
	 * package is already provided
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param debPackage
	 * @param packageInstaller
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installPackage(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final File debPackage,
			final PackageInstaller packageInstaller) throws SSHConnectionException, CommandExecutionException {

		logger.info("Installing package remotely on: {} with username: {}", new Object[] { ip, username });

		// Update package list first!
		SetupUtils.executeCommand(ip, username, password, port, privateKey, passphrase, UPDATE_PACKAGE_LIST);

		copyFile(ip, username, password, port, privateKey, passphrase, debPackage, "/tmp/");

		String command;

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);

		manager.connect();

		if (packageInstaller == PackageInstaller.DPKG) {
			command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + debPackage.getName());
		} else {
			command = INSTALL_PACKAGE_GDEBI.replace("{0}", "/tmp/" + debPackage.getName());
			manager.execCommand(INSTALL_GDEBI, new Object[] {});
		}

		manager.execCommand(command, new Object[] {});
		manager.disconnect();

		logger.info("Package {} installed successfully", debPackage.getName());
	}

	/**
	 * Installs a deb package file. This can be used when a specified deb
	 * package is already provided. Before installing the package it uses
	 * debconf-set-selections to insert default values which asked during the
	 * interactive installation.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param debPackage
	 * @param debconfValues
	 */
	public static void installPackageNonInteractively(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final File debPackage,
			final String[] debconfValues, final PackageInstaller packageInstaller) throws Exception {
		// Update package list first!
		SetupUtils.executeCommand(ip, username, password, port, privateKey, passphrase, UPDATE_PACKAGE_LIST);

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		// Set frontend as noninteractive
		manager.execCommand(SET_DEBIAN_FRONTEND, new Object[] {});

		manager.disconnect();
		manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
		manager.connect();

		// Set debconf values
		for (String value : debconfValues) {
			manager.execCommand(DEBCONF_SET_SELECTIONS, new Object[] { value });
		}

		manager.disconnect();

		// Finally, install the package
		SetupUtils.installPackage(ip, username, password, port, privateKey, passphrase, debPackage, packageInstaller);
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param packageName
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void uninstallPackage(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String packageName)
			throws CommandExecutionException, SSHConnectionException {
		logger.info("Uninstalling package remotely on: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();
		manager.execCommand(UNINSTALL_PACKAGE_CMD, new Object[] { packageName });
		manager.disconnect();

		logger.info("Package {} uninstalled successfully", packageName);
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param repository
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void addRepository(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String repository)
			throws CommandExecutionException, SSHConnectionException {
		logger.info("Adding repository remotely on: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();
		manager.execCommand(ADD_APP_REPO_CMD, new Object[] { repository });
		manager.disconnect();

		logger.info("Repository {} added successfully", repository);
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param fileToTranster
	 * @param destDirectory
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void copyFile(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final File fileToTranster, final String destDirectory)
			throws SSHConnectionException, CommandExecutionException {
		String destinationDir = destDirectory;
		if (!destinationDir.endsWith("/")) {
			destinationDir += "/";
		}

		logger.info("Copying file to: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();
		manager.copyFileToRemote(fileToTranster, destinationDir, false);
		manager.disconnect();

		logger.info("File {} copied successfully", fileToTranster.getName());
	}

	/**
	 * 
	 * Executes a command on the given machine.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param command
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void executeCommand(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String command)
			throws SSHConnectionException, CommandExecutionException {
		logger.info("Executing command remotely on: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		manager.execCommand(command, new Object[] {});
		logger.info("Executed command successfully: {}", new Object[] { command });

		manager.disconnect();
	}

	public static void executeCommand(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String command,
			IOutputStreamProvider outputStreamProvider) throws SSHConnectionException, CommandExecutionException {
		executeCommand(ip, username, password, port, privateKey, passphrase, command, outputStreamProvider, true);
	}

	public static void executeCommand(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String command,
			IOutputStreamProvider outputStreamProvider, boolean usePty)
			throws SSHConnectionException, CommandExecutionException {
		logger.info("Executing command remotely on: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		manager.execCommand(command, null, outputStreamProvider, usePty);
		logger.info("Executed command successfully: {}", new Object[] { command });

		manager.disconnect();
	}

	/**
	 * Installs a deb package which has been downloaded before by
	 * downloadPackage method. It searches the file in /tmp/{tmpDir} folder.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param tmpDir
	 * @param filename
	 * @param packageInstaller
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installDownloadedPackage(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String filename,
			final PackageInstaller packageInstaller) throws SSHConnectionException, CommandExecutionException {

		logger.info("Installing package remotely on: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);

		manager.connect();

		String command;

		if (packageInstaller == PackageInstaller.DPKG) {
			// Prepare command
			if (!"".equals(filename)) {
				command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + filename);
			} else {
				command = INSTALL_PACKAGE.replace("{0}", "/tmp/*.deb");
			}
		} else {
			if (!"".equals(filename)) {
				command = INSTALL_PACKAGE_GDEBI.replace("{0}", "/tmp/" + filename);
			} else {
				command = INSTALL_PACKAGE_GDEBI.replace("{0}", "/tmp/*.deb");
			}
			manager.execCommand(INSTALL_GDEBI, new Object[] {});
		}

		manager.execCommand(command, new Object[] {});
		manager.disconnect();

		logger.info("Package {} installed successfully", filename);
	}

	/**
	 * Downloads a file from given URL to given machine with provided name under
	 * /tmp.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param filename
	 * @param downloadUrl
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void downloadPackage(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String filename,
			final String downloadUrl) throws SSHConnectionException, CommandExecutionException {

		String command;

		if (filename == null || "".equals(filename)) {
			command = DOWNLOAD_PACKAGE.replace("{0}", downloadUrl);
		} else {
			command = DOWNLOAD_PACKAGE_WITH_FILENAME.replace("{0}", filename).replace("{1}", downloadUrl);
		}

		logger.info("Executing command remotely on: {} with username: {}", new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		manager.execCommand(command, new Object[] {});
		logger.info("Command: '{}' executed successfully.", new Object[] { command });

		manager.disconnect();
	}

	/**
	 * Installs a deb package which has been downloaded before by
	 * downloadPackage method. Before installing the package it uses
	 * debconf-set-selections to insert default values which asked during the
	 * interactive installation. It searches the file in /tmp folder.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param debPackage
	 * @param debconfValues
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installDownloadedPackageNonInteractively(final String ip, final String username,
			final String password, final Integer port, final String privateKey, final String passphrase,
			final String filename, final String[] debconfValues, final PackageInstaller packageInstaller)
			throws SSHConnectionException, CommandExecutionException {
		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		// Set frontend as noninteractive
		manager.execCommand(SET_DEBIAN_FRONTEND, new Object[] {});

		manager.disconnect();
		manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
		manager.connect();

		// Set debconf values
		for (String value : debconfValues) {
			manager.execCommand(DEBCONF_SET_SELECTIONS, new Object[] { value });
		}

		manager.disconnect();

		// Finally, install the downloaded package
		SetupUtils.installDownloadedPackage(ip, username, password, port, privateKey, passphrase, filename,
				packageInstaller);
	}

}