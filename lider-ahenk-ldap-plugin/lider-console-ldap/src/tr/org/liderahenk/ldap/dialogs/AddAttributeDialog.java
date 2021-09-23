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
import org.eclipse.swt.widgets.Combo;
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
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderTitleAreaDialog;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.enums.RestResponseStatus;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.LiderConfirmBox;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.widgets.NotifierColorsFactory.NotifierTheme;

/**
 * Task execution dialog for ldap plugin.
 * 
 */
public class AddAttributeDialog extends DefaultLiderTitleAreaDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(AddAttributeDialog.class);
	
	private String dn;
	private Text textDesc;
	private ProgressBar progressBar;
	private Label lblEqual;
	private Label lblAttrLabel;
	private Label lblValue;
	private Combo combo;
	
	public AddAttributeDialog(Shell parentShell, String dn) {
		super(parentShell);
		this.dn=dn;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("add_attribute"));
        setMessage("Lütfen Öznitelik ve Değerini Girin ", IMessageProvider.INFORMATION);
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		//shell.setText(Messages.getString("add_attribute"));
	}
	
	
	public Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));	
		
		GridData gridData= new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.widthHint = 600;
		gridData.heightHint = 200;
		
		composite.setLayoutData(gridData);
		
		lblAttrLabel = new Label(composite, SWT.NONE);
		lblAttrLabel.setAlignment(SWT.CENTER);
		lblAttrLabel.setText(Messages.getString("attribute")); //$NON-NLS-1$
		new Label(composite, SWT.NONE);
		
		lblValue = new Label(composite, SWT.NONE);
		lblValue.setAlignment(SWT.CENTER);
		lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblValue.setText(Messages.getString("value"));
		
		combo = new Combo(composite, SWT.NONE);
		combo.setItems(new String[] {   "sudoUser", "sudoHost", "sudoCommand", 
										"ou","cn","sn","dn","uid","description", "gIdNumber","uidNumber", "mail"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(-1);
		
		lblEqual = new Label(composite, SWT.NONE);
		lblEqual.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEqual.setText(Messages.getString("equal")); //$NON-NLS-1$
		
		textDesc = new Text(composite, SWT.BORDER);
		textDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData gdProgress = new GridData(GridData.FILL_HORIZONTAL);
		gdProgress.grabExcessHorizontalSpace = false;
		gdProgress.horizontalSpan = 3;
		gdProgress.heightHint = 10;
		progressBar.setLayoutData(gdProgress);
		progressBar.setVisible(false);
		
		return composite;
	
	}

	
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		

		if (LiderConfirmBox.open(Display.getDefault().getActiveShell(), Messages.getString("TASK_EXEC_TITLE"),
				Messages.getString("TASK_EXEC_MESSAGE"))) {
			try {
				progressBar.setVisible(true);

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
				progressBar.setVisible(false);
				
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			} catch (Exception e1) {
				progressBar.setVisible(false);
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

		return dnList;
	}
	
	public Map<String, Object> getParameterMap() {
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("attribute",combo.getText());
		map.put("dn", dn);
		map.put("value", textDesc.getText());
		return map;
	}

	public String getCommandId() {
		return "ADD_ATTRIBUTE";
	}

	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	
}
