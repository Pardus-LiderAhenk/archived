package tr.org.liderahenk.packagemanager.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.packagemanager.dialogs.CheckPackageTaskDialog;

public class CheckPackageTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		CheckPackageTaskDialog dialog = new CheckPackageTaskDialog(Display.getDefault().getActiveShell(), dnSet, true);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
