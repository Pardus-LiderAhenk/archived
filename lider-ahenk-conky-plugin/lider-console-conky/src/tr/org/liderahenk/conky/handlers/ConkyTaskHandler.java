package tr.org.liderahenk.conky.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.conky.dialogs.ConkyTaskCommandDialog;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

public class ConkyTaskHandler extends MultipleSelectionHandler {
	
	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		ConkyTaskCommandDialog dialog = new ConkyTaskCommandDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.open();
	}
}
