package tr.org.liderahenk.remote.access.listeners;

import com.glavsoft.rfb.protocol.ProtocolSettings;
import com.glavsoft.viewer.ConnectionPresenter;
import com.glavsoft.viewer.UiSettings;
import com.glavsoft.viewer.cli.Parser;
import com.glavsoft.viewer.swing.ConnectionParams;
import com.glavsoft.viewer.swing.ParametersHandler;
import com.glavsoft.viewer.swing.SwingConnectionWorkerFactory;
import com.glavsoft.viewer.swing.SwingViewerWindowFactory;
import com.glavsoft.viewer.swing.WrongParameterException;
import com.glavsoft.viewer.swing.gui.ConnectionView;
import com.glavsoft.viewer.Viewer;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteAccessConnection implements Runnable, WindowListener {

	private static final Logger logger = LoggerFactory.getLogger(RemoteAccessConnection.class);

	private int paramsMask;
	private final ConnectionParams connectionParams;
	private static String[] serverParameters;
	boolean isSeparateFrame = true;
	private final ProtocolSettings settings;
	private final UiSettings uiSettings;
	private ConnectionPresenter connectionPresenter;

	public RemoteAccessConnection() {
		connectionParams = new ConnectionParams();
		settings = ProtocolSettings.getDefaultSettings();
		uiSettings = new UiSettings();
	}

	private RemoteAccessConnection(Parser parser) {
		this();
		paramsMask = ParametersHandler.completeSettingsFromCLI(parser, connectionParams, settings, uiSettings);
	}

	public static void invoke(String hostName, String portNumber, String password) {

		serverParameters = new String[] { hostName, portNumber, password };

		Parser parser = new Parser();
		ParametersHandler.completeParserOptions(parser);

		RemoteAccessConnection connection = new RemoteAccessConnection(parser);
		SwingUtilities.invokeLater(connection);
	}

	private boolean checkJsch() {
		try {
			Class.forName("com.jcraft.jsch.JSch");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void run() {

		connectionParams.setHostName(serverParameters[0]);
		try {
			connectionParams.setPortNumber(serverParameters[1]);
		} catch (WrongParameterException e) {
			logger.error(e.toString(), e);
		}
		final boolean hasJsch = checkJsch();
		connectionPresenter = new ConnectionPresenter(hasJsch, true);
		connectionPresenter.addModel("ConnectionParamsModel", connectionParams);

		final ConnectionView connectionView = new ConnectionView(RemoteAccessConnection.this, connectionPresenter,
				false);

		connectionPresenter.addView(ConnectionPresenter.CONNECTION_VIEW, connectionView);
		connectionView.closeView();

		SwingViewerWindowFactory viewerWindowFactory = new SwingViewerWindowFactory(isSeparateFrame, true,
				new Viewer());

		connectionPresenter.setConnectionWorkerFactory(
				new SwingConnectionWorkerFactory(null, serverParameters[2], connectionPresenter, viewerWindowFactory));

		connectionPresenter.startConnection(settings, uiSettings, paramsMask);

		try {
			connectionPresenter.submitConnection(serverParameters[0]);
			if (connectionPresenter.needReconnection())
				connectionView.closeView();
		} catch (WrongParameterException e) {
			logger.error(e.toString(), e);
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}