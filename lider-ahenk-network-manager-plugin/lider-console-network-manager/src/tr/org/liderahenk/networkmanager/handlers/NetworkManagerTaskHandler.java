package tr.org.liderahenk.networkmanager.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.networkmanager.dialogs.NetworkManagerTaskDialog;

public class NetworkManagerTaskHandler extends SingleSelectionHandler {
	
	@Override
	public void executeWithDn(String dn) {
		NetworkManagerTaskDialog dialog = new NetworkManagerTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}
}
