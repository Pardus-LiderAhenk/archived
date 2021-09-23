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
public class AddHostDialog extends DefaultTaskDialog {
	
	private Text txtIp;
	private Text txtHostname;
	private Button btnActive;

	public AddHostDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
	}

	@Override
	public String createTitle() {
		return Messages.getString("NEW_HOST");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
		GridData gridData =  new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(gridData);
		
		Label lblIp = new Label(composite, SWT.NONE);
		lblIp.setText(Messages.getString("IP"));
		
		txtIp = new Text(composite, SWT.BORDER);
		txtIp.setLayoutData(gridData);
		
		Label lblHostname = new Label(composite, SWT.NONE);
		lblHostname.setText(Messages.getString("HOSTNAME"));
		
		txtHostname = new Text(composite, SWT.BORDER);
		txtHostname.setLayoutData(gridData);
		
		btnActive = new Button(composite, SWT.CHECK);
		btnActive.setText(Messages.getString("ACTIVE"));
		btnActive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (txtIp.getText() == null || txtIp.getText().replaceAll("\\s+","").isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_IP_FIELD"));
		}
		if (txtHostname.getText() == null || txtHostname.getText().replaceAll("\\s+","").isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_HOSTNAME_FIELD"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(NetworkManagerConstants.PARAMETERS.IP, txtIp.getText());
		parameterMap.put(NetworkManagerConstants.PARAMETERS.HOSTNAME, txtHostname.getText());
		parameterMap.put(NetworkManagerConstants.PARAMETERS.IS_ACTIVE, btnActive.getSelection());
		return parameterMap;
	}

	@Override
	public String getCommandId() {
		return "ADD_HOST";
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