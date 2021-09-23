package tr.org.liderahenk.ldap.handlers;


import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.dialogs.DeleteAttributeDialog;
import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;

//TODO use MultipleSelectionHandler if this task support multiple LDAP entries/DNs otherwise use SingleSelectionHandler.
public class DeleteAttributeHandler extends SingleSelectionHandler {

	private Logger logger = LoggerFactory.getLogger(DeleteAttributeHandler.class);
	
	@Override
	public void executeWithDn(String dn) {

		
	}
	
	@Override
	public void executeWithDnWithParam(String dn, Map<String, Object> params) {
		// TODO Auto-generated method stub
		//super.executeWithDnWithParam(dn, params);
		
		DeleteAttributeDialog dialog = new DeleteAttributeDialog(Display.getDefault().getActiveShell(), dn,params);
		dialog.create();
		dialog.open();
	}
	
	
	
}