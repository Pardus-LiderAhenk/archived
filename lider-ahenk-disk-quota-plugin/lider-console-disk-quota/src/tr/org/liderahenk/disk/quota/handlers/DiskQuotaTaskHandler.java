package tr.org.liderahenk.disk.quota.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.disk.quota.dialogs.DiskQuotaTaskDialog;

public class DiskQuotaTaskHandler extends SingleSelectionHandler {
	
	@Override
	public void executeWithDn(String dn) {
		DiskQuotaTaskDialog dialog = new DiskQuotaTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}
}
