package tr.org.liderahenk.packagemanager.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.packagemanager.dialogs.PackageManagementTaskDialog;

public class PackageManagerTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		PackageManagementTaskDialog dialog = new PackageManagementTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}
	
}