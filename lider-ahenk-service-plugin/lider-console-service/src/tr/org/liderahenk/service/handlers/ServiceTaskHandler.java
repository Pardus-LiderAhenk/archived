package tr.org.liderahenk.service.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.service.dialogs.ServiceTaskDialog;

public class ServiceTaskHandler extends MultipleSelectionHandler {
	
	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		ServiceTaskDialog dialog = new ServiceTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.open();
	}
	
}
