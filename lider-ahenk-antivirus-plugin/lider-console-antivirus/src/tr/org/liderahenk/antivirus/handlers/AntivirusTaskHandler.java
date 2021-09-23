package tr.org.liderahenk.antivirus.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.antivirus.dialogs.AntivirusTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

public class AntivirusTaskHandler extends MultipleSelectionHandler {
	
	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		AntivirusTaskDialog dialog = new AntivirusTaskDialog(Display.getDefault().getActiveShell(), dnSet, true);
		dialog.create();
		dialog.open();
	}
	
}
