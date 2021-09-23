package tr.org.liderahenk.backup.dialogs;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * Task execution dialog for backup plugin.
 * 
 *  @author Seren Piri <seren.piri@gmail.com>
 */
public class BackupTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(BackupTaskDialog.class);
	private BackupDialogBase backupDialog = new BackupDialogBase();
	
	public BackupTaskDialog(Shell parentShell, Set<String> dnSet)
	{
		super(parentShell, dnSet);
		logger.debug("Backup Task Editor Initialization");
	}

	@Override
	public String createTitle() {
		return "Backup";
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		backupDialog.createBackupDialog(parent, null);
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		logger.debug("Backup Task - Validation is in progress.");
		backupDialog.validateProfile();
	}

	@Override
	public Map<String, Object> getParameterMap() {
		return backupDialog.getParameterData();
	}

	@Override
	public String getCommandId() {
		return "BACKUP_TASK";
	}

	@Override
	public String getPluginName() {
		return BackupConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return BackupConstants.PLUGIN_VERSION;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(600, 700);
	}
	
}
