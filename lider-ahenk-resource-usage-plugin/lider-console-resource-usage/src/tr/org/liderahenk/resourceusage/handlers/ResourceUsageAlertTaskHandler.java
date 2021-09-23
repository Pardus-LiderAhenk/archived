package tr.org.liderahenk.resourceusage.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.resourceusage.dialogs.ResourceUsageAlertTaskDialog;

public class ResourceUsageAlertTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		ResourceUsageAlertTaskDialog dialog = new ResourceUsageAlertTaskDialog(Display.getDefault().getActiveShell(),
				dn);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
