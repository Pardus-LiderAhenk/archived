package tr.org.liderahenk.ldap.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.constants.LdapConstants;
import tr.org.liderahenk.ldap.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.dialogs.LiderLdapTreeDialog;
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
public class MoveUserDialog extends DefaultLiderDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(MoveUserDialog.class);
	
	
	private String dn;
	
	private Label lblInfo;
	
	
	private Combo combo;

	private Button btnParent;

	private Button btnOpenLiderTree;

	
	private ProgressBar progressBar;

	
	public MoveUserDialog(Shell parentShell, String dn) {
		super(parentShell);
		this.dn=dn;
	}
	
	@Override
	public void create() {
		super.create();
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("delete"));
	}
	
	
	public Control createDialogArea(Composite parent) {
		

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblInfo = new Label(composite, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		lblInfo.setText(Messages.getString("info"));
		
		Label lblParentEntry = new Label(composite, SWT.NONE);
		lblParentEntry.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblParentEntry.setText(Messages.getString("parent_entry"));
		
		combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
//		btnParent = new Button(composite, SWT.NONE);
//		btnParent.setText(Messages.getString("parent"));
		
		btnOpenLiderTree = new Button(composite, SWT.NONE);
		btnOpenLiderTree.setText(Messages.getString("tree"));
		
		btnOpenLiderTree.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				openLiderLdapTree();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData gdProgress = new GridData(GridData.FILL_HORIZONTAL);
		gdProgress.grabExcessVerticalSpace = true;
		gdProgress.horizontalSpan = 2;
		gdProgress.heightHint = 10;
		progressBar.setLayoutData(gdProgress);
		progressBar.setVisible(false);
		
		return composite;
	
	}
	
	
	protected void openLiderLdapTree() {
		
		LiderLdapTreeDialog dialog= new  LiderLdapTreeDialog(this.getShell());
		dialog.create();
		if (dialog.open() == Window.OK) {
		   String selectedDn= dialog.getSelectedEntryDn();
		   combo.setText(selectedDn);
		}
		
	}
	
	@Override
	protected void okPressed() {

		setReturnCode(OK);
		if (this.dn==null
		)
		{
			Notifier.notifyandShow(null, "", Messages.getString("MANDATORY_FIELD"), "", NotifierTheme.ERROR_THEME);
			return;
		}

		if (LiderConfirmBox.open(Display.getDefault().getActiveShell(), Messages.getString("TASK_EXEC_TITLE"),
				Messages.getString("TASK_EXEC_MESSAGE"))) {
			try {
				progressBar.setVisible(true);

				TaskRequest task = new TaskRequest(getDnSet(), DNType.USER, getPluginName(), getPluginVersion(),
						getCommandId(), getParameterMap(), null, null, new Date());
				
				IResponse response = TaskRestUtils.execute(task);
				
				if (response != null && response.getStatus() == RestResponseStatus.OK) {
					Notifier.success(null, Messages.getString("TASK_EXECUTED"));
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
		dnList.add(dn);

		return dnList;
	}
	
	public Map<String, Object> getParameterMap() {
		
		String newParentDn=combo.getText();
		
		Map<String, Object> map = new HashMap<>();
		map.put("dn", dn);
		map.put("newParentDn", newParentDn);
		
		return map;
	}

	public String getCommandId() {
		return "MOVE_USER";
	}

	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	
}
