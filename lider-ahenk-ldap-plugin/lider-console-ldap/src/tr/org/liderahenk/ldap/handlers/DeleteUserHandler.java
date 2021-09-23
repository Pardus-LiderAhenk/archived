package tr.org.liderahenk.ldap.handlers;


import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.dialogs.DeleteUserDialog;
import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;

//TODO use MultipleSelectionHandler if this task support multiple LDAP entries/DNs otherwise use SingleSelectionHandler.
public class DeleteUserHandler extends SingleSelectionHandler {

	private Logger logger = LoggerFactory.getLogger(DeleteUserHandler.class);
	
	@Override
	public void executeWithDn(String dn) {

		DeleteUserDialog dialog = new DeleteUserDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.open();
	}
	
	
}