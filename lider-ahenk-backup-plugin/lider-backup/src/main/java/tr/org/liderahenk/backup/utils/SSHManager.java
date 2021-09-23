package tr.org.liderahenk.backup.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import tr.org.liderahenk.lider.core.api.exceptions.SSHConnectionException;
import tr.org.liderahenk.lider.core.api.utils.IOutputStreamProvider;

/**
 * SSHManager is responsible for managing SSH connections and executing
 * commands.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class SSHManager {

	private static final Logger logger = LoggerFactory.getLogger(SSHManager.class);

	private JSch SSHChannel;
	private Session session;
	private Properties config;

	private String username;
	private String password;
	private String ip;
	private int port;
	private String privateKey;
	private String passphrase;

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 */
	public SSHManager(String ip, String username, String password, Integer port, String privateKey, String passphrase) {
		init();
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.port = port;
		this.privateKey = privateKey;
		this.passphrase = passphrase;
	}

	/**
	 * Initializes SSH manager by configuring encryption algorithms and setting
	 * SSH logger.
	 */
	private void init() {
		JSch.setLogger(new DefaultSSHLogger());
		SSHChannel = new JSch();
		config = new Properties();
		config.put("kex",
				"diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
		config.put("StrictHostKeyChecking", "no");
	}

	/**
	 * Tries to connect via SSH key or username-password pair
	 * 
	 * @throws SSHConnectionException
	 *             if it fails to connect
	 */
	public void connect() throws SSHConnectionException {
		try {
			if (privateKey != null && !privateKey.isEmpty()) {
				if (passphrase != null && !passphrase.isEmpty()) {
					SSHChannel.addIdentity(privateKey, passphrase.getBytes(StandardCharsets.UTF_8));
				} else {
					SSHChannel.addIdentity(privateKey);
				}
			}
			session = SSHChannel.getSession(username, ip, port);
			if (password != null && !password.isEmpty()) {
				session.setPassword(password);
			}
			session.setConfig(config);
			session.connect();
		} catch (JSchException e) {
			logger.error(e.getMessage(), e);
			throw new SSHConnectionException(e.getMessage());
		}
	}

	/**
	 * Executes command string via SSH
	 * 
	 * @param command
	 *            Command String
	 * @param outputStreamProvider
	 *            Provides an array of bytes which is used to pass arguments to
	 *            the command executed.
	 * @return
	 * @return output of the executed command
	 * @throws RuntimeException
	 * 
	 */
	public String execCommand(final String command, final IOutputStreamProvider outputStreamProvider, boolean usePty)
			throws RuntimeException {

		Channel channel = null;

		logger.info("Command: {}", command);

		StringBuilder output = new StringBuilder();

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// Open channel and handle output stream
			InputStream inputStream = channel.getInputStream();
			((ChannelExec) channel).setPty(usePty);

			OutputStream outputStream = null;
			byte[] byteArray = null;
			if (outputStreamProvider != null) {
				outputStream = channel.getOutputStream();
				byteArray = outputStreamProvider.getStreamAsByteArray();
			}

			channel.connect();

			// Pass provided byte array as command argument
			if (outputStream != null && byteArray != null) {
				outputStream.write(byteArray);
				outputStream.flush();
			}

			// Read output from the stream and handle the channel accordingly
			byte[] tmp = new byte[1024];
			while (true) {
				while (inputStream.available() > 0) {
					int i = inputStream.read(tmp, 0, 1024);
					if (i < 0)
						break;
					output.append(new String(tmp, 0, i, StandardCharsets.UTF_8));
				}
				if (channel.isClosed()) {
					logger.info("exit status: " + channel.getExitStatus());
					if (channel.getExitStatus() != 0) {
						throw new RuntimeException("Exit status: " + channel.getExitStatus());
					}

					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}

		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			throw new RuntimeException(e1.getMessage());
		} finally {
			if (channel != null) {
				try {
					channel.disconnect();
				} catch (Exception e) {
				}
			}
		}
		logger.info(output.toString());

		return output.toString();
	}

	/**
	 * Executes command string via SSH. Replaces parameter indicators with
	 * values from the provided array before execution.
	 * 
	 * @param command
	 * @param params
	 * @return output of the executed command
	 * @throws RuntimeException
	 */
	public String execCommand(final String command, final Object[] params) throws RuntimeException {
		return execCommand(command, params, null, true);
	}

	/**
	 * Executes command string via SSH. Replaces parameter indicators with
	 * values from the provided array before execution. While executing the
	 * command feeds its output stream via IOutputStreamProvider instance
	 * 
	 * @param command
	 * @param params
	 * @param outputStreamProvider
	 * @return output of the executed command
	 * @throws RuntimeException
	 */
	public String execCommand(final String command, final Object[] params, IOutputStreamProvider outputStreamProvider, boolean usePty)
			throws RuntimeException {
		String tmpCommand = command;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				String param = params[i].toString();
				tmpCommand = tmpCommand.replace("{" + i + "}", param);
			}
		}
		return execCommand(tmpCommand, outputStreamProvider, usePty);
	}

	public void disconnect() {
		session.disconnect();
	}

	public JSch getSSHChannel() {
		return SSHChannel;
	}

	public Session getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getPassphrase() {
		return passphrase;
	}

}
