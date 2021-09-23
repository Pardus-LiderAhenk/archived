package tr.org.liderahenk.networkmanager.dialogs;

import java.util.HashMap;
import java.util.Map;

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
public class AddNetworkDialog extends DefaultTaskDialog {
	
	private Combo cmbType;
	private Text txtName;
	private Label lblIp;
	private Label lblNetmask;
	private Label lblGateway;
	private Text txtIp;
	private Text txtNetmask;
	private Text txtGateway;
	private Button btnActive;
	
	private final String[] arrTypes = new String[] { "STATIC", "LOOPBACK", "DHCP" };

	public AddNetworkDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
	}

	@Override
	public String createTitle() {
		return Messages.getString("NEW_NETWORK_INTERFACE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
		GridData gridData =  new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(gridData);
		
		Label lblType = new Label(composite, SWT.NONE);
		lblType.setText(Messages.getString("TYPE"));
		
		cmbType = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < arrTypes.length; i++) {
			cmbType.add(arrTypes[i]);
		}
		
		cmbType.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!cmbType.getText().equals("STATIC")) {
					lblIp.setEnabled(false);
					txtIp.setEnabled(false);
					
					lblNetmask.setEnabled(false);
					txtNetmask.setEnabled(false);
					
					lblGateway.setEnabled(false);
					txtGateway.setEnabled(false);
				}
				else {
					lblIp.setEnabled(true);
					txtIp.setEnabled(true);
					
					lblNetmask.setEnabled(true);
					txtNetmask.setEnabled(true);
					
					lblGateway.setEnabled(true);
					txtGateway.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Label lblName = new Label(composite, SWT.NONE);
		lblName.setText(Messages.getString("NAME"));
		
		txtName = new Text(composite, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		lblIp = new Label(composite, SWT.NONE);
		lblIp.setText(Messages.getString("ADDRESS"));
		
		txtIp = new Text(composite, SWT.BORDER);
		txtIp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		lblNetmask = new Label(composite, SWT.NONE);
		lblNetmask.setText(Messages.getString("NETMASK"));
		
		txtNetmask = new Text(composite, SWT.BORDER);
		txtNetmask.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		lblGateway = new Label(composite, SWT.NONE);
		lblGateway.setText(Messages.getString("GATEWAY"));
		
		txtGateway = new Text(composite, SWT.BORDER);
		txtGateway.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		btnActive = new Button(composite, SWT.CHECK);
		btnActive.setText(Messages.getString("ACTIVE"));
		btnActive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (cmbType.getText() == null || cmbType.getText().replaceAll("\\s+","").isEmpty()) {
			throw new ValidationException(Messages.getString("SELECT_TYPE"));
		}
		if (txtName.getText() == null || txtName.getText().replaceAll("\\s+","").isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_NAME_FIELD"));
		}
		if (cmbType.getText().equals("STATIC")) {
			if (txtIp.getText().replaceAll("\\s+","").isEmpty()) {
				throw new ValidationException(Messages.getString("FILL_IP_FIELD"));
			}
			if (txtNetmask.getText().replaceAll("\\s+","").isEmpty()) {
				throw new ValidationException(Messages.getString("FILL_NETMASK_FIELD"));
			}
			if (txtGateway.getText().replaceAll("\\s+","").isEmpty()) {
				throw new ValidationException(Messages.getString("FILL_GATEWAY_FIELD"));
			}
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(NetworkManagerConstants.PARAMETERS.TYPE, cmbType.getText());
		
		if (cmbType.getText().equals("STATIC")) {
			parameterMap.put(NetworkManagerConstants.PARAMETERS.IP, txtIp.getText());
			parameterMap.put(NetworkManagerConstants.PARAMETERS.NETMASK, txtNetmask.getText());
			parameterMap.put(NetworkManagerConstants.PARAMETERS.GATEWAY, txtGateway.getText());
		}
		
		parameterMap.put(NetworkManagerConstants.PARAMETERS.NAME, txtName.getText());
		parameterMap.put(NetworkManagerConstants.PARAMETERS.IS_ACTIVE, btnActive.getSelection());
		return parameterMap;
	}

	@Override
	public String getCommandId() {
		return "ADD_NETWORK";
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
