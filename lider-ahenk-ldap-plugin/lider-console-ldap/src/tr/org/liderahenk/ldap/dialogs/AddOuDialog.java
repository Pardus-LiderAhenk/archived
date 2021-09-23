package tr.org.liderahenk.ldap.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.constants.LdapConstants;
import tr.org.liderahenk.ldap.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderTitleAreaDialog;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.enums.RestResponseStatus;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.LiderConfirmBox;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.widgets.NotifierColorsFactory.NotifierTheme;
import org.eclipse.swt.custom.CLabel;

/**
 * Task execution dialog for ldap plugin.
 * 
 */
public class AddOuDialog extends DefaultLiderTitleAreaDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(AddOuDialog.class);
	
	
	private String dn;
	
	private Text text;
	private Text textDesc;
	
	public AddOuDialog(Shell parentShell, String dn) {
		super(parentShell);
		this.dn=dn;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("add_ou"));
        setMessage("Seçilen kayda organizasyon birimi ekleyebilirsiniz.", IMessageProvider.INFORMATION);
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("add_ou"));
	}
	
	
	public Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));	
		
		GridData gridData= new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.widthHint = 600;
		gridData.heightHint = 200;
		
		composite.setLayoutData(gridData);
		
//		Label info = new Label(composite, SWT.NONE);
//		info.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
//		info.setText("SeÃ§ili deÄerin altÄ±na organizasyon birimi ekleyebilirsiniz.");
		
		Label ouNmaeLabel = new Label(composite, SWT.NONE);
		ouNmaeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		ouNmaeLabel.setText(Messages.getString("ou_name"));
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label descLabel = new Label(composite, SWT.NONE);
		descLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		descLabel.setText(Messages.getString("description"));
		
		textDesc = new Text(composite, SWT.BORDER);
		textDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		return composite;
	
	}

	
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		if (text != null && text.getText()=="") {
			
			Notifier.notifyandShow(null, "", Messages.getString("ENTER_NEW_PASSWORD"), "", NotifierTheme.ERROR_THEME);
			return;
		}

		if (LiderConfirmBox.open(Display.getDefault().getActiveShell(), Messages.getString("TASK_EXEC_TITLE"),
				Messages.getString("TASK_EXEC_MESSAGE"))) {
			try {
				//progressBar.setVisible(true);

				TaskRequest task = new TaskRequest(getDnSet(), DNType.USER, getPluginName(), getPluginVersion(),
						getCommandId(), getParameterMap(), null, null, new Date());
				
				IResponse response = TaskRestUtils.execute(task);
				
				if (response != null && response.getStatus() == RestResponseStatus.OK) {
					Notifier.notifyandShow(null, 
							"", 
							Messages.getString("TASK_RESULT"), 
							Messages.getString("SUCCESS_ON_EXECUTE"), NotifierTheme.SUCCESS_THEME);
					} else if (response != null && response.getStatus() == RestResponseStatus.ERROR) {
					if (response.getMessages() != null && !response.getMessages().isEmpty()) {
						Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"),
								StringUtils.join(response.getMessages(), ""));
					} else {
						Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
					}
				}
			//	progressBar.setVisible(false);
				
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			} catch (Exception e1) {
				//progressBar.setVisible(false);
				logger.error(e1.getMessage(), e1);
				Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
			}
		}

	}
	
	@Override
	protected void cancelPressed() {
		close();
	}
	
	private List<String> getDnSet() {

		List<String> dnList = new ArrayList<>();
		dnList.add(dn);

		return dnList;
	}
	
	public Map<String, Object> getParameterMap() {
		
		Map<String, Object> map = new HashMap<>();
		map.put("ou_name",text.getText());
		map.put("dn", dn);
		map.put("desc", textDesc.getText());
		return map;
	}

	public String getCommandId() {
		return "ADD_OU";
	}

	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	
}