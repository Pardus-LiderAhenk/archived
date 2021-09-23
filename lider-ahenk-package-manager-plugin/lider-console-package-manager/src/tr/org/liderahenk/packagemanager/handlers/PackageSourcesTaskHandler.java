package tr.org.liderahenk.packagemanager.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.packagemanager.dialogs.PackageSourcesTaskDialog;

public class PackageSourcesTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		PackageSourcesTaskDialog dialog = new PackageSourcesTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
