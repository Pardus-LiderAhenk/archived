package tr.org.liderahenk.networkmanager.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.networkmanager.constants.NetworkManagerConstants;
import tr.org.liderahenk.networkmanager.i18n.Messages;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class AddDNSDialog extends DefaultTaskDialog {
	
	private String title;
	private String commandId;
	
	private Text txtIp;
	private Button btnActive;
	private Text txtDomain;

	public AddDNSDialog(Shell parentShell, String dn, String title, String commandId) {
		super(parentShell, dn);
		this.title = title;
		this.commandId = commandId;
	}

	@Override
	public String createTitle() {
		return Messages.getString(title);
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
		GridData gridData =  new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(gridData);
		
		if (commandId.equals("ADD_DNS")) {
			Label lblIp = new Label(composite, SWT.NONE);
			lblIp.setText(Messages.getString("IP"));
			
			txtIp = new Text(composite, SWT.BORDER);
			txtIp.setLayoutData(gridData);
			
			btnActive = new Button(composite, SWT.CHECK);
			btnActive.setText(Messages.getString("ACTIVE"));
			btnActive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		}
		else if (commandId.equals("ADD_DOMAIN")) {
			Label lblDomain = new Label(composite, SWT.NONE);
			lblDomain.setText(Messages.getString("DOMAIN"));
			
			txtDomain = new Text(composite, SWT.BORDER);
			txtDomain.setLayoutData(gridData);
		}
		
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (commandId.equals("ADD_DNS")) {
			if (txtIp.getText() == null || txtIp.getText().replaceAll("\\s+","").isEmpty()) {
				throw new ValidationException(Messages.getString("FILL_IP_FIELD"));
			}
		}
		else if (commandId.equals("ADD_DOMAIN")) {
			if (txtDomain.getText() == null || txtDomain.getText().replaceAll("\\s+","").isEmpty()) {
				throw new ValidationException(Messages.getString("FILL_DOMAIN_FIELD"));
			}
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		if (commandId.equals("ADD_DNS")) {
			parameterMap.put(NetworkManagerConstants.PARAMETERS.IP, txtIp.getText());
			parameterMap.put(NetworkManagerConstants.PARAMETERS.IS_ACTIVE, btnActive.getSelection());
		}
		else if (commandId.equals("ADD_DOMAIN")) {
			parameterMap.put(NetworkManagerConstants.PARAMETERS.DOMAIN, txtDomain.getText());
		}
		return parameterMap;
	}

	@Override
	public String getCommandId() {
		return commandId;
	}

	@Override
	public String getPluginName() {
		return NetworkManagerConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return NetworkManagerConstants.PLUGIN_VERSION;
	}

}
