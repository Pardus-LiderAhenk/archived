package tr.org.liderahenk.usb.ltsp.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.editors.LiderManagementEditor;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.usb.ltsp.dialogs.UsbFuseGroupDialog;

public class UsbFuseGroupHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		
		String user=LiderManagementEditor.selectedUserDn;
		UsbFuseGroupDialog dialog = new UsbFuseGroupDialog(Display.getDefault().getActiveShell(), dnSet, user);
		dialog.create();
		dialog.open();
	}

}
