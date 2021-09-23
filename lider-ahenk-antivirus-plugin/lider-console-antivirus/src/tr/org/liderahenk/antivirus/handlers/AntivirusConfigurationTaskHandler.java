package tr.org.liderahenk.antivirus.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.antivirus.dialogs.AntivirusConfigurationTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;

public class AntivirusConfigurationTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		AntivirusConfigurationTaskDialog dialog = new AntivirusConfigurationTaskDialog(Display.getDefault().getActiveShell(), dn, true);
		dialog.create();
		dialog.openWithEventBroker();
	}
}