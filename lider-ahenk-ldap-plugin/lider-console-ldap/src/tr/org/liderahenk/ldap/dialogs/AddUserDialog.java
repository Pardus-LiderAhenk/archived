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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
public class AddUserDialog extends DefaultLiderTitleAreaDialog {


	private static final Logger logger = LoggerFactory.getLogger(AddUserDialog.class);


	private String dn;

	private Text textName;
	private Text textSurname;
	private ProgressBar progressBar;

	private Text textUid;
	private Text textGid;
	private Text textUidNumber;
	private Text textPassword;
	private Text textPasswordRepeat;

	private Button btnUIDNumberIncrease;
	private Button btnUIDNumberDecrease;

	private Button btnGIDNumberIncrease;
	private Button btnGIDNumberDecrease;
	
	public AddUserDialog(Shell parentShell, String dn) {
		super(parentShell);
		this.dn=dn;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("add_user"));
        setMessage("Seçilen kayda kullanıcı ekleyebilirsiniz.", IMessageProvider.INFORMATION);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		//shell.setText(Messages.getString("add_user"));
	}


	public Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, GridData.FILL);
		composite.setLayout(new GridLayout(1, false));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 100;
		composite.setLayout(gridLayout);

		GridData data= new GridData(SWT.FILL, SWT.FILL, true, true,1,1);
		data.widthHint=650;
		data.heightHint=240;

		composite.setLayoutData(data);

		//File Path Label
		
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 100;
		
//		Label info = new Label(composite, SWT.NONE);
//		info.setLayoutData(gridData);
//		info.setText("Seçili değerin altına kullanıcı ekleyebilirsiniz."); //$NON-NLS-1$

		Label nameLabel = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 10;
		gridData.heightHint = 20;
		nameLabel.setLayoutData(gridData);
		nameLabel.setText("Ad (cn) :"); //$NON-NLS-1$

		textName = new Text(composite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 90;
		gridData.heightHint = 20;
		textName.setLayoutData(gridData);

		Label surname = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 10;
		surname.setLayoutData(gridData);
		surname.setText("Soyad (sn) :"); //$NON-NLS-1$

		textSurname = new Text(composite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 90;
		gridData.heightHint = 20;
		textSurname.setLayoutData(gridData);

		Label uid = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 10;
		gridData.heightHint = 20;
		uid.setLayoutData(gridData);
		uid.setText("Kimlik (uid) :");

		textUid = new Text(composite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 90;
		gridData.heightHint = 20;
		textUid.setLayoutData(gridData);

		Label gid = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 10;
		gridData.heightHint = 20;
		gid.setLayoutData(gridData);
		gid.setText("Grup Id (gid) :");

		textGid = new Text(composite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 80;
		gridData.heightHint = 20;
		textGid.setLayoutData(gridData);
		textGid.setText("6000");
		textGid.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		btnGIDNumberIncrease = new Button(composite, SWT.CENTER);
		gridData = new GridData(SWT.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 5;
		gridData.heightHint = 20;
		btnGIDNumberIncrease.setText("+");
		btnGIDNumberIncrease.setLayoutData(gridData);
		btnGIDNumberIncrease.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(textGid.getText().equals("")) {
					textGid.setText("0");
				}
				else {
					int gidNumber = Integer.valueOf(textGid.getText().toString());
					textGid.setText(String.valueOf(gidNumber+1));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		btnGIDNumberDecrease = new Button(composite, SWT.CENTER);
		gridData = new GridData(SWT.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 5;
		gridData.heightHint = 20;
		btnGIDNumberDecrease.setText("-");
		btnGIDNumberDecrease.setLayoutData(gridData);
		btnGIDNumberDecrease.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(textGid.getText().equals("")) {
					textGid.setText("0");
				}
				else {
					int gidNumber = Integer.valueOf(textGid.getText().toString());
					gidNumber = gidNumber -1;
					if(gidNumber <= 0) {
						textGid.setText("0");
					}
					else {
						textGid.setText(String.valueOf(gidNumber));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

//		Label uidNumber = new Label(composite, SWT.NONE);
//		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
//		gridData.horizontalSpan = 10;
//		gridData.heightHint = 20;
//		uidNumber.setLayoutData(gridData);
//		uidNumber.setText("Uid Number :");
//
//		textUidNumber = new Text(composite, SWT.BORDER);
//		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
//		gridData.horizontalSpan = 80;
//		gridData.heightHint = 20;
//		textUidNumber.setLayoutData(gridData);
//		textUidNumber.setText("6000");
//		textUidNumber.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9')) {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		btnUIDNumberIncrease = new Button(composite, SWT.CENTER);
//		gridData = new GridData(SWT.FILL, GridData.CENTER, true, false);
//		gridData.horizontalSpan = 5;
//		gridData.heightHint = 20;
//		btnUIDNumberIncrease.setText("+");
//		btnUIDNumberIncrease.setLayoutData(gridData);
//		btnUIDNumberIncrease.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent arg0) {
//				if(textUidNumber.getText().equals("")) {
//					textUidNumber.setText("0");
//				}
//				else {
//					int uidNumber = Integer.valueOf(textUidNumber.getText().toString());
//					textUidNumber.setText(String.valueOf(uidNumber+1));
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent arg0) {
//
//			}
//		});
//
//
//		btnUIDNumberDecrease = new Button(composite, SWT.CENTER);
//		gridData = new GridData(SWT.FILL, GridData.CENTER, true, false);
//		gridData.horizontalSpan = 5;
//		gridData.heightHint = 20;
//		btnUIDNumberDecrease.setText("-");
//		btnUIDNumberDecrease.setLayoutData(gridData);
//		btnUIDNumberDecrease.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent arg0) {
//				if(textUidNumber.getText().equals("")) {
//					textUidNumber.setText("0");
//				}
//				else {
//					int uidNumber = Integer.valueOf(textUidNumber.getText().toString());
//					uidNumber = uidNumber -1;
//					if(uidNumber <= 0) {
//						textUidNumber.setText("0");
//					}
//					else {
//						textUidNumber.setText(String.valueOf(uidNumber));
//					}
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent arg0) {
//
//			}
//		});
		Label password = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 10;
		gridData.heightHint = 20;
		password.setLayoutData(gridData);
		password.setText("Parola :");

		textPassword = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 90;
		gridData.heightHint = 20;
		textPassword.setLayoutData(gridData);
		
		Label passwordRepeat = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 10;
		gridData.heightHint = 20;
		passwordRepeat.setLayoutData(gridData);
		passwordRepeat.setText("Parola Tekrarı:");

		textPasswordRepeat = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		gridData = new GridData(SWT.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 90;
		gridData.heightHint = 20;
		textPasswordRepeat.setLayoutData(gridData);

		Label passwordRule = new Label(composite, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, GridData.CENTER, true, false);
		gridData.horizontalSpan = 100;
		gridData.heightHint = 20;
		passwordRule.setLayoutData(gridData);
		passwordRule.setText(Messages.getString("PASSWORD_RULE"));
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		gridData.horizontalSpan = 100;
		gridData.heightHint = 10;
		gridData.grabExcessVerticalSpace = true;
		progressBar.setLayoutData(gridData);
		progressBar.setVisible(false);

		return composite;
	}

	@Override
	protected void okPressed() {

		setReturnCode(OK);
		if ((textName != null && textName.getText().equals("") )
				|| (textSurname != null && textSurname.getText().equals("") ) 
				|| (textGid != null && textGid.getText().equals("")) 
				|| (textUid != null && textUid.getText().equals("")) 
				|| (textPassword != null && textPassword.getText().equals(""))
				|| (textPasswordRepeat != null && textPasswordRepeat.getText().equals(""))) {
			Notifier.notifyandShow(null, "", Messages.getString("MANDATORY_FIELD"), "", NotifierTheme.ERROR_THEME);
			return;
		} else {
			if(!textPassword.getText().equals(textPasswordRepeat.getText())) {
				Notifier.notifyandShow(null, "", Messages.getString("PASSWORDS_MISMATCH"), "", NotifierTheme.ERROR_THEME);
				return;
			} else {
				if(!textPassword.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{10,}$")) {
					Notifier.notifyandShow(null, "", Messages.getString("PASSWORD_RULE_ERROR"), "", NotifierTheme.ERROR_THEME);
					return;
				}
			}
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

		Map<String, Object> map = new HashMap<>();
		map.put("dn", dn);
		map.put("cn",textName.getText());
		map.put("gidNumber",textGid.getText());
		map.put("sn",textSurname.getText());
		map.put("uid",textUid.getText());
		//map.put("uidNumber", textUidNumber.getText());
		map.put("password", textPassword.getText());
		return map;
	}

	public String getCommandId() {
		return "ADD_USER";
	}

	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}


}
