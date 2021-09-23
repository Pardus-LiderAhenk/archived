package tr.org.liderahenk.password.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
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
import tr.org.liderahenk.password.model.PasswordPolicy;

/**
 * Task execution dialog for password plugin.
 * 
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cemre ALPSOY</a>
 * 
 */
public class AddPasswordPolicyDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(AddPasswordPolicyDialog.class);
	private Label lblPassword;
	private ProgressBar progressBar;

	private String selectedUser;
	private List<String> selectedUserList;

	private Set<String> dnSet;
	private Label lblDnInfo;
	private ListViewer dnlistViewer;
	private Combo combo;
	private Combo comboPolicyList;
	private ComboViewer comboViewer;

	/**
	 * @wbp.parser.constructor
	 */
	public AddPasswordPolicyDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell);
		this.dnSet = dnSet;

	}

	public AddPasswordPolicyDialog(Shell parentShell, Set<String> dnSet, String selectedUser, List<String> selectedUserList) {
		super(parentShell);

		this.selectedUser = selectedUser;
		this.selectedUserList = selectedUserList;
		this.dnSet = dnSet;
		
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("ADD_LDAP_PASSWORD_POLICY"));
	}
	
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(600, 300);
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

		if (dnSet != null && !dnSet.isEmpty())
		{
			dnList = new ArrayList<>();
			
			
			dnList.addAll(dnSet);
			
			if (dnList != null && dnList.size() > 0) {

//				List<String> targetEntries = LdapUtils.getInstance().findUsers(dnList.get(0)); // secili
//																								// entry
//																								// nin
//																								// tum
//																								// child
//																								// entryleri
//																								// bulunur.
//				if (targetEntries.size() == 0)
//					targetEntries = LdapUtils.getInstance().findAgents(dnList.get(0)); // ahenkler
//																						// icin
//																						// ve
//																						// kullanıcılar
//																						// icin
//																						// gecerli
//																						// olabilir.
				dnlistViewer.setInput(dnList);
				lblDnInfo.setText(Messages.getString("selected_entry_size") + " : " + dnList.size());

			}
		}
		else if(selectedUserList!=null && selectedUserList.size()>0) {
			
			dnlistViewer.setInput(selectedUserList);
			lblDnInfo.setText(Messages.getString("selected_entry_size") + " : " + selectedUserList.size());

			
		}

		

		lblPassword = new Label(composite, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		lblPassword.setText(Messages.getString("password_policy_list"));
		
		comboViewer = new ComboViewer(composite, SWT.NONE);
		comboPolicyList = comboViewer.getCombo();
		comboPolicyList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		comboViewer.setLabelProvider(new LabelProvider() {
		        @Override
		        public String getText(Object element) {
		            if (element instanceof PasswordPolicy) {
		            	PasswordPolicy current = (PasswordPolicy) element;

		                return current.getShortName();
		            }
		            return super.getText(element);
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
		
		
		
		TaskRequest task = new TaskRequest(null, DNType.USER, getPluginName(), getPluginVersion(),
				"GET_PASSWORD_POICIES", null , null, null, new Date());
		
		try {
			IResponse response = TaskRestUtils.execute(task);
			
			
			List<LinkedHashMap<String, Object>> policyList= (List<LinkedHashMap<String, Object>>) response.getResultMap().get("policyList");

			List<PasswordPolicy> policyListesi= new ArrayList<>();
			
			for (LinkedHashMap<String, Object> policyMap : policyList) {
				
				PasswordPolicy passwordPolicy= new PasswordPolicy();
				
				passwordPolicy.setDn((String) policyMap.get("distinguishedName"));
				
				policyListesi.add(passwordPolicy);
			}

			comboViewer.setInput(policyListesi);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//passwordPolicyList= 

		return null;
	}

	@Override
	protected void okPressed() {

		setReturnCode(OK);

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if(comboViewer.getSelection().isEmpty()){
			Notifier.notifyandShow(null, "", Messages.getString("SELECT_PASSWORD_POLICY"), "", NotifierTheme.ERROR_THEME);
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
				e1.printStackTrace();
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
		
		List<String> dnList = null;

		if (dnSet != null && !dnSet.isEmpty())
		{
			dnList = new ArrayList<>();
			dnList.addAll(dnSet);
		}

//		if (selectedUser == null && selectedUserList!=null && selectedUserList.size()>0)
//			return selectedUserList;
//		else {
//
//			List<String> dnList = new ArrayList<>();
//			dnList.add(selectedUser);
//		
//
//			return dnList;
//		}
		return dnList;
	}

	public Map<String, Object> getParameterMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
	IStructuredSelection selection=	 (IStructuredSelection) comboViewer.getSelection();
	
	PasswordPolicy passwordPolicy= (PasswordPolicy) selection.getFirstElement();
	
		
			map.put(PasswordConstants.PASSWORD_POLICY, passwordPolicy.getDn());
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
		return "ADD_PASSWORD_POLICY";
	}

	public String getPluginName() {
		return PasswordConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return PasswordConstants.PLUGIN_VERSION;
	}

}
