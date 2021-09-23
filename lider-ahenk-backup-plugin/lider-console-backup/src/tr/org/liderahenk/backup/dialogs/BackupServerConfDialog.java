package tr.org.liderahenk.backup.dialogs;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.model.BackupServerConf;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class BackupServerConfDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(BackupServerConfDialog.class);

	private Text txtUsername;
	private Text txtPassword;
	private Text txtDestHost;
	private Spinner spnDestPort;
	private Text txtDestPath;

	private BackupServerConf selectedConfig = null;

	public BackupServerConfDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		try {
			selectedConfig = getBackupServerConfig();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parent.setLayout(new GridLayout(1, false));

		Label lbl = new Label(parent, SWT.NONE);
		lbl.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lbl.setText(Messages.getString("BACKUP_SERVER_CONF_LABEL"));

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(new GridLayout(4, false));

		Label labelUN = new Label(composite, SWT.NONE);
		labelUN.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelUN.setText(Messages.getString("USERNAME"));
		txtUsername = new Text(composite, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (selectedConfig != null && selectedConfig.getUsername() != null) {
			txtUsername.setText(selectedConfig.getUsername());
		}

		Label labelPassword = new Label(composite, SWT.NONE);
		labelPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPassword.setText(Messages.getString("PASSWORD"));
		txtPassword = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		if (selectedConfig != null && selectedConfig.getPassword() != null) {
			txtPassword.setText(selectedConfig.getPassword());
		}

		Label labelHost = new Label(composite, SWT.NONE);
		labelHost.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelHost.setText(Messages.getString("DEST_HOST"));
		txtDestHost = new Text(composite, SWT.BORDER);
		txtDestHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (selectedConfig != null && selectedConfig.getDestHost() != null) {
			txtDestHost.setText(selectedConfig.getDestHost());
		}

		Label labelPort = new Label(composite, SWT.NONE);
		labelPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPort.setText(Messages.getString("DEST_PORT"));
		spnDestPort = new Spinner(composite, SWT.BORDER);
		spnDestPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spnDestPort.setMinimum(0);
		spnDestPort.setMaximum(65535);
		spnDestPort.setSelection(BackupConstants.DEFAULT_PORT_INT);
		if (selectedConfig != null && selectedConfig.getDestPort() != null) {
			spnDestPort.setSelection(selectedConfig.getDestPort());
		}

		Label labelDir = new Label(composite, SWT.NONE);
		labelDir.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelDir.setText(Messages.getString("DEST_DIR"));
		txtDestPath = new Text(composite, SWT.BORDER);
		txtDestPath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtDestPath.setText(BackupConstants.DEFAULT_DEST_PATH);
		if (selectedConfig != null && selectedConfig.getDestPath() != null) {
			txtDestPath.setText(selectedConfig.getDestPath());
		}

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Handle OK button press
	 */
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		if (txtUsername.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_USERNAME_FIELD"));
			return;
		}
		if (txtPassword.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_PASSWORD_FIELD"));
			return;
		}
		if (txtDestHost.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_DEST_HOST_FIELD"));
			return;
		}
		if (txtDestPath.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_DEST_PATH_FIELD"));
			return;
		}

		BackupServerConf config = new BackupServerConf();
		config.setUsername(txtUsername.getText());
		config.setPassword(txtPassword.getText());
		config.setDestPort(spnDestPort.getSelection());
		config.setDestPath(txtDestPath.getText());
		config.setDestHost(txtDestHost.getText());
		config.setCreateDate(new Date());
		if (selectedConfig != null) {
			config.setId(selectedConfig.getId());
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("BACKUP_SERVER_CONFIG", config);
		TaskRequest task = new TaskRequest(null, null, BackupConstants.PLUGIN_NAME, BackupConstants.PLUGIN_VERSION,
				"SAVE_BACKUP_SERVER_CONFIG", parameterMap, null, null, new Date());
		logger.debug("Backup server config request: {}", task);

		try {
			IResponse response = TaskRestUtils.execute(task);
			selectedConfig = new ObjectMapper().readValue(response.getResultMap().get("BACKUP_SERVER_CONFIG").toString(), BackupServerConf.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_SAVE"));
		}

		close();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(1200, 230);
	}

	private BackupServerConf getBackupServerConfig() throws JsonParseException, JsonMappingException, IOException {
		IResponse response = null;
		try {
			response = TaskRestUtils.execute(BackupConstants.PLUGIN_NAME, BackupConstants.PLUGIN_VERSION,
					"GET_BACKUP_SERVER_CONFIG", false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
		return (BackupServerConf) ((response != null && response.getResultMap() != null
				&& response.getResultMap().get("BACKUP_SERVER_CONFIG") != null)
						? new ObjectMapper().readValue(response.getResultMap().get("BACKUP_SERVER_CONFIG").toString(),
								BackupServerConf.class)
						: null);
	}

	public BackupServerConf getSelectedConfig() {
		return selectedConfig;
	}

}
