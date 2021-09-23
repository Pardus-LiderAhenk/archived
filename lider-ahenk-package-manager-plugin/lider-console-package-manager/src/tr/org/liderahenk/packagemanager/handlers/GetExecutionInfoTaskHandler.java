package tr.org.liderahenk.packagemanager.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.packagemanager.dialogs.GetExecutionInfoTaskDialog;

public class GetExecutionInfoTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		GetExecutionInfoTaskDialog dialog = new GetExecutionInfoTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.openWithEventBroker();
	}

}
