package tr.org.liderahenk.password.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.ldap.utils.LdapUtils;
import tr.org.liderahenk.liderconsole.core.rest.enums.RestResponseStatus;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.LiderConfirmBox;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.widgets.NotifierColorsFactory.NotifierTheme;
import tr.org.liderahenk.password.constants.PasswordConstants;
import tr.org.liderahenk.password.i18n.Messages;

/**
 * Task execution dialog for password plugin.
 * 
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cemre ALPSOY</a>
 * 
 */
public class PasswordTaskDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(PasswordTaskDialog.class);
	private Label lblPassword;
	private Text txtPassword;
	
	private Label lblPasswordRepeat;
	private Text txtPasswordRepeat;
	
	private Label lblPasswordRule;
	
	private ProgressBar progressBar;

	private String selectedUser;
	
	private List<String> selectedUserList;

	private Set<String> dnSet;
	private Label lblDnInfo;
	private ListViewer dnlistViewer;

	/**
	 * @wbp.parser.constructor
	 */
	public PasswordTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell);
		this.dnSet = dnSet;

	}

	public PasswordTaskDialog(Shell parentShell, Set<String> dnSet, String selectedUser,List<String> selectedUserList) {
		super(parentShell);

		this.selectedUser = selectedUser;
		this.selectedUserList = selectedUserList;
		this.dnSet = dnSet;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("CHANGE_LDAP_PASSWORD"));
	}

	@Override
	public Control createDialogArea(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = SWT.DEFAULT;
		gd.heightHint = SWT.DEFAULT;
		composite.setLayoutData(gd);

		lblDnInfo = new Label(composite, SWT.NONE);
		lblDnInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		dnlistViewer = new ListViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		org.eclipse.swt.widgets.List list = dnlistViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 2, 1));

		dnlistViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(Object inputElement) {
				List<String> v = (List<String>) inputElement;
				return v.toArray();
			}
		});

		List<String> dnList = null;

		if (selectedUser != null)

		{
			dnList = new ArrayList<>();
			dnList.add(selectedUser);
			
			if (dnList != null && dnList.size() > 0) {

				List<String> targetEntries = LdapUtils.getInstance().findUsers(dnList.get(0)); // secili
																								// entry
																								// nin
																								// tum
																								// child
																								// entryleri
																								// bulunur.

				if (targetEntries.size() == 0)
					targetEntries = LdapUtils.getInstance().findAgents(dnList.get(0)); // ahenkler
																						// icin
																						// ve
																						// kullanıcılar
																						// icin
																						// gecerli
																						// olabilir.

				dnlistViewer.setInput(targetEntries);
				lblDnInfo.setText(Messages.getString("selected_entry_size") + " : " + targetEntries.size());

			}
		}
		else if(selectedUserList!=null && selectedUserList.size()>0) {
			
			dnlistViewer.setInput(selectedUserList);
			lblDnInfo.setText(Messages.getString("selected_entry_size") + " : " + selectedUserList.size());

			
		}

		lblPassword = new Label(composite, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblPassword.setText(Messages.getString("PASSWORD"));

		txtPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		lblPasswordRepeat = new Label(composite, SWT.NONE);
		lblPasswordRepeat.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblPasswordRepeat.setText(Messages.getString("PASSWORD_REPEAT"));

		txtPasswordRepeat = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtPasswordRepeat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		
		
		lblPasswordRule = new Label(composite, SWT.NONE);
		lblPasswordRule.setText(Messages.getString("PASSWORD_RULE"));
		lblPasswordRule.setLayoutData(new GridData(SWT.LEFT,SWT.LEFT,false,true,3,1));
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData gdProgress = new GridData(GridData.FILL_HORIZONTAL);
		gdProgress.grabExcessVerticalSpace = true;
		gdProgress.horizontalSpan = 2;
		gdProgress.heightHint = 10;
		progressBar.setLayoutData(gdProgress);
		progressBar.setVisible(false);

		return null;
	}

	@Override
	protected void okPressed() {

		setReturnCode(OK);

		HashMap<String, Object> map = new HashMap<String, Object>();
		if ((txtPassword != null && txtPassword.getText() != null && !txtPassword.getText().isEmpty()) &&
				(txtPasswordRepeat != null && txtPasswordRepeat.getText() != null && !txtPasswordRepeat.getText().isEmpty()))
			if(txtPassword.getText().equals(txtPasswordRepeat.getText())) {
				if(!txtPassword.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{10,}$")) {
					Notifier.notifyandShow(null, "", Messages.getString("PASSWORD_RULE_ERROR"), "", NotifierTheme.ERROR_THEME);
					return;
				}
				else {
					map.put(PasswordConstants.PASSWORD, txtPassword.getText().toString());
				}
			}
			else {
				Notifier.notifyandShow(null, "", Messages.getString("PASSWORDS_MISMATCH"), "", NotifierTheme.ERROR_THEME);
				return;
			}
			
		else {
			Notifier.notifyandShow(null, "", Messages.getString("ENTER_NEW_PASSWORD"), "", NotifierTheme.ERROR_THEME);
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

		if (selectedUser == null && selectedUserList!=null && selectedUserList.size()>0)
			return selectedUserList;
		else {

			List<String> dnList = new ArrayList<>();
			dnList.add(selectedUser);

			return dnList;
		}
	}

	public Map<String, Object> getParameterMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (txtPassword != null && txtPassword.getText() != null && !txtPassword.getText().isEmpty())
			map.put(PasswordConstants.PASSWORD, txtPassword.getText().toString());
		return map;
	}

	// @Override
	// protected void createButtonsForButtonBar(Composite parent) {
	// // Execute task now
	// btnExecuteNow = createButton(parent, 5000,
	// Messages.getString("CHANGE_NOW"), false);
	// btnExecuteNow.setImage(
	// SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE,
	// "icons/16/task-play.png"));
	// btnExecuteNow.addSelectionListener(new SelectionListener() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// // Validation of task data
	// if (true) {}
	// }
	//
	// @Override
	// public void widgetDefaultSelected(SelectionEvent e) {
	// }
	// });
	//
	//
	// }

	public String getCommandId() {
		return "CHANGE_LDAP_PASSWORD";
	}

	public String getPluginName() {
		return PasswordConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return PasswordConstants.PLUGIN_VERSION;
	}

}
