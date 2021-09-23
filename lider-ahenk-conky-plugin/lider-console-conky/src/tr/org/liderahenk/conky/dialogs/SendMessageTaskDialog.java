package tr.org.liderahenk.conky.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.conky.constants.ConkyConstants;
import tr.org.liderahenk.conky.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

public class SendMessageTaskDialog extends DefaultTaskDialog{

	private Label lblMessage;
	private Text textMessage;
	private Label label;
	private Combo comboTimeout;
	private Label lblNewLabel;
	
	private String selectedUserDn;

	public SendMessageTaskDialog(Shell parentShell, Set<String> dnSet,  String user) {
		super(parentShell, dnSet);
		
		this.selectedUserDn=user;
		
	}

	@Override
	public String createTitle() {
		return Messages.getString("TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
	
		Composite composite = new Composite(parent, GridData.FILL);
		
		composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        lblMessage = new Label(composite, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblMessage.setText(Messages.getString("MESSAGE")); //$NON-NLS-1$
        
        textMessage = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_text.heightHint = 82;
       // gd_text.widthHint = 282;
        textMessage.setLayoutData(gd_text);
        
        
//        label = new Label(composite, SWT.NONE);
//        label.setText(Messages.getString("TIMEOUT"));
        
//        comboTimeout = new Combo(composite, SWT.NONE);
//        GridData gd_comboTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
//        gd_comboTimeout.widthHint = 83;
//        comboTimeout.setLayoutData(gd_comboTimeout);
//        createTimeoutCombo(comboTimeout);
        
//        lblNewLabel = new Label(composite, SWT.NONE);
//        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
//        lblNewLabel.setText(Messages.getString("sn"));
		
		return composite;
	
		
		
	}
	
	private void createTimeoutCombo(Composite composite)
	{
		String[] times=new String[60];
		for (int i = 0; i < 60; i++) {
			times[i]=new Integer(i+1).toString();
		}
		
		comboTimeout.setItems(times);
		comboTimeout.select(9);
	
	}
	
	@Override
	public void validateBeforeExecution() throws ValidationException {
		

	//	if(textSettings.getText().equals("")) throw new ValidationException(Messages.getString("FILL_FIELDS"));
	
		
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> map = new HashMap<>();
		
		if(selectedUserDn!=null){
			
			String[] selectedUserDnArr=selectedUserDn.split(",");
			
			if(selectedUserDnArr.length>0){
				String[] selectedUserArr= selectedUserDnArr[0].split("=");
				
				if(selectedUserArr.length>1){
					
					String selectedUser= selectedUserArr[1];
					map.put("selected_user",selectedUser );
				}
			}
		}
		
		String message = textMessage.getText();
	//	String timeout = comboTimeout.getText();
		
		message= message.replace('\n', ' ');
		
		map.put("message", message );
	//	map.put("timeout", timeout );
		
		return map;
	}

	@Override
	public String getCommandId() {
		return "EXECUTE_XMESSAGE";
	}

	@Override
	public String getPluginName() {
		return ConkyConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ConkyConstants.PLUGIN_VERSION;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}

}
