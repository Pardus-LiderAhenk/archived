package tr.org.liderahenk.packagemanager.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.packagemanager.dialogs.PackageArchiveTaskDialog;

public class PackageArchiveTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		PackageArchiveTaskDialog dialog = new PackageArchiveTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
