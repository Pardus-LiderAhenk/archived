package tr.org.liderahenk.loginmanager.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.loginmanager.dialogs.LoginManagerTaskDialog;

public class LoginManagerTaskHandler extends MultipleSelectionHandler {
	
	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		LoginManagerTaskDialog dialog = new LoginManagerTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.open();
	}
	
}
