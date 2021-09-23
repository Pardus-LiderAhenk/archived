package tr.org.liderahenk.backup.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.backup.dialogs.BackupTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

public class BackupTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		BackupTaskDialog dialog = new BackupTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.open();
	}

}
