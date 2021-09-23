package tr.org.liderahenk.backup.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.backup.dialogs.RestoreTaskDialog2;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

public class RestoreTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		RestoreTaskDialog2 dialog = new RestoreTaskDialog2(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
