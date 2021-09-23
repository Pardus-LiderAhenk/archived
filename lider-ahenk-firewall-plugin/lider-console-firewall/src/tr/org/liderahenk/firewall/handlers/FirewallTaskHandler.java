package tr.org.liderahenk.firewall.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.firewall.dialogs.FirewallTaskDialog;

public class FirewallTaskHandler extends SingleSelectionHandler {
	
	@Override
	public void executeWithDn(String dn) {
		FirewallTaskDialog dialog = new FirewallTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}
}
