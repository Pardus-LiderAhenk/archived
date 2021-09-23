package tr.org.liderahenk.ldap.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.constants.LdapConstants;
import tr.org.liderahenk.ldap.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.dialogs.LiderLdapTreeDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * Task execution dialog for ldap plugin.
 * 
 */
public class DeleteAgentDialog extends DefaultTaskDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(DeleteAgentDialog.class);
	
	private Label lblInfo;
	
	private String dn;

	private String cn;
	
	public DeleteAgentDialog(Shell parentShell, String dn) {
		super(parentShell, dn,true);
		this.dn=dn;
		setCnValue();
	}
	
	@Override
	public void create() {
		super.create();
	}
	

	@Override
	public String createTitle() {
		return Messages.getString("delete");
	}

		
	@Override
	public Control createTaskDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblInfo = new Label(composite, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		lblInfo.setText(Messages.getString("delete_str"));
		
		return composite;
	}


	@Override
	public void validateBeforeExecution() throws ValidationException {
		
	}
	
	
	@Override
	public Map<String, Object> getParameterMap() {
		
		Map<String, Object> map = new HashMap<>();
		map.put("dn", dn);
		return map;
	}

	@Override
	public String getCommandId() {
		return "DELETE_AGENT";
	}

	@Override
	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	
	private void setCnValue() {
		cn="";
		if(dn!=null && !dn.equals("")) {
			String[] dnStr= dn.split(",");
			if(dnStr.length>1) {
				cn=dnStr[0]+",";
			}
		}
		
	}
}
