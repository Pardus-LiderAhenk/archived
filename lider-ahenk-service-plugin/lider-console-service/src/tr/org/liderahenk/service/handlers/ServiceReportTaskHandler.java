package tr.org.liderahenk.service.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.service.dialogs.ServiceReportTaskDialog;

public class ServiceReportTaskHandler extends SingleSelectionHandler{

	@Override
	public void executeWithDn(String dn) {
		ServiceReportTaskDialog dialog = new ServiceReportTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
