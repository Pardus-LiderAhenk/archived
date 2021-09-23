package tr.org.liderahenk.service.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.service.dialogs.ServiceListTaskDialog;

public class ServiceListTaskHandler extends SingleSelectionHandler{

	@Override
	public void executeWithDn(String dn) {
		ServiceListTaskDialog dialog = new ServiceListTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
