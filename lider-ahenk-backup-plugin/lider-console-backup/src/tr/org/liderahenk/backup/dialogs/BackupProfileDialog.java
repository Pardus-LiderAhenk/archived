package tr.org.liderahenk.backup.dialogs;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;

public class BackupProfileDialog implements IProfileDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(BackupProfileDialog.class);
	private BackupDialogBase backupDialog = new BackupDialogBase();
	
	@Override
	public void init() {
		logger.debug("Backup Plugin - Profile Editor is Started.");
	}
	
	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		logger.debug("Backup Profile recieved: {} ", profile != null ? profile.toString() : null);
		if (profile == null) {
			backupDialog.createBackupDialog(parent, null);
		} else {
			backupDialog.createBackupDialog(parent, profile.getProfileData());
		}
	}
	
	@Override
	public Map<String, Object> getProfileData() {
		Map<String, Object> data = backupDialog.getParameterData();
		logger.debug("Backup Profile - Profile Data recieved: {} ", data);
		return data;
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
		logger.debug("Backup Profile - Validation is in progress.");
		backupDialog.validateProfile();
	}
	
}
