package tr.org.liderahenk.ldap.handlers;


import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.dialogs.AddAttributeDialog;
import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;

//TODO use MultipleSelectionHandler if this task support multiple LDAP entries/DNs otherwise use SingleSelectionHandler.
public class AddAttributeHandler extends SingleSelectionHandler {

	private Logger logger = LoggerFactory.getLogger(AddAttributeHandler.class);
	
	@Override
	public void executeWithDn(String dn) {

		AddAttributeDialog dialog = new AddAttributeDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.open();
	}
	
	
}