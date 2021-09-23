package tr.org.liderahenk.wol.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.wol.dialogs.WakeMachineTaskDialog;;

public class WakeMachineTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		WakeMachineTaskDialog dialog = new WakeMachineTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.open();
	}
}
