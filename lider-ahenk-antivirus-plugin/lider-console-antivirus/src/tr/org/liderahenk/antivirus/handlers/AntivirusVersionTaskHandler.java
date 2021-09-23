package tr.org.liderahenk.antivirus.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.antivirus.dialogs.AntivirusVersionTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;

public class AntivirusVersionTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		AntivirusVersionTaskDialog dialog = new AntivirusVersionTaskDialog(Display.getDefault().getActiveShell(), dn, true);
		dialog.create();
		dialog.openWithEventBroker();
	}
}