package tr.org.liderahenk.usb.ltsp.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.usb.ltsp.dialogs.AgentUsbFuseGroupResultDialog;

public class AgentUsbFuseGroupResultHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		AgentUsbFuseGroupResultDialog dialog = new AgentUsbFuseGroupResultDialog(Display.getDefault().getActiveShell(),
				dn);
		dialog.create();
		dialog.open();
	}

}
