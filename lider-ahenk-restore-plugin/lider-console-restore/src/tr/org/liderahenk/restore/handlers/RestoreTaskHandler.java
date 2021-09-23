package tr.org.liderahenk.restore.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.restore.dialogs.RestoreTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

public class RestoreTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		RestoreTaskDialog dialog = new RestoreTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
