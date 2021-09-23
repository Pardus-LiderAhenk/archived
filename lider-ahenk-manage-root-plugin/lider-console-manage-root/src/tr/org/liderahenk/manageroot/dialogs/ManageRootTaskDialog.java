package tr.org.liderahenk.manageroot.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.manageroot.constants.ManageRootConstant;
import tr.org.liderahenk.manageroot.i18n.Messages;
import tr.org.liderahenk.manageroot.password.PasswordGenerator;

/**
 * Task execution dialog for manage-root plugin.
 * 
 */
public class ManageRootTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(ManageRootTaskDialog.class);
	private Text textRootPassword;
	private Button btnLockRootUser;
	private Button btnGeneratePassword;
	private Shell shell;
	private Label lblRootPassword;
	private Label lblPasswordRule;
	private Label lblAllowedCharacters;
	private Boolean isLockRootUserChecked;
	// TODO do not forget to change this constructor if SingleSelectionHandler is used!
	public ManageRootTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet,true,true);
		shell = parentShell;
		isLockRootUserChecked = false;
	}

	@Override
	public String createTitle() {
		return Messages.getString("TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		GridData  gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = SWT.DEFAULT;
		gd.heightHint = SWT.DEFAULT;
		composite.setLayoutData(gd);
		
		//checkbox for locking root
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = SWT.DEFAULT;
		gd.heightHint = SWT.DEFAULT;
		composite.setLayoutData( gd);
		
		btnLockRootUser=new Button(composite, SWT.CHECK| SWT.BORDER);
		btnLockRootUser.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,3,1));
		btnLockRootUser.setText(Messages.getString("LOCK_ROOT_USER_CHECK_BOX"));
		
		Label lblLockRootUserInfo = new Label(composite, SWT.NONE);
		lblLockRootUserInfo.setText(Messages.getString("LOCK_ROOT_USER_INFO"));
		lblLockRootUserInfo.setLayoutData(new GridData(SWT.LEFT,SWT.LEFT,false,true,3,1));
		
		Label lblSpace = new Label(composite, SWT.NONE);
		lblSpace.setText("");
		lblSpace.setLayoutData(new GridData(SWT.LEFT,SWT.LEFT,false,true,3,1));
		
		lblRootPassword = new Label(composite, SWT.NONE);
		lblRootPassword.setText(Messages.getString("SET_ROOT_PASS"));
		
		textRootPassword = new Text(composite, SWT.BORDER);
		GridData gd_RootPassword = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		textRootPassword.setLayoutData(gd_RootPassword);
			
		//generate auto password
		btnGeneratePassword = new Button(composite, SWT.CENTER);
		btnGeneratePassword.setText(Messages.getString("GENERATE_PASSWORD"));
		btnGeneratePassword.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,true,1,1));
		
		lblPasswordRule = new Label(composite, SWT.NONE);
		lblPasswordRule.setText(Messages.getString("PASSWORD_RULE"));
		lblPasswordRule.setLayoutData(new GridData(SWT.LEFT,SWT.LEFT,false,true,3,1));
		
		lblAllowedCharacters = new Label(composite, SWT.NONE);
		lblAllowedCharacters.setText(Messages.getString("ALLOWED_PUNCTUATIONS") + ": " + PasswordGenerator.PUNCTUATION);
		lblAllowedCharacters.setLayoutData(new GridData(SWT.LEFT,SWT.LEFT,false,true,3,1));
		
		btnGeneratePassword.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
				        .useDigits(true)
				        .useLower(true)
				        .useUpper(true)
				        .usePunctuation(true)
				        .build();
				textRootPassword.setText(passwordGenerator.generate(12));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnLockRootUser.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button buttonResultOfCheckBox = (Button) e.getSource() ;
				if(buttonResultOfCheckBox.getSelection() == true) {
					isLockRootUserChecked = true;
					textRootPassword.setText("");
					textRootPassword.setEnabled(false);
					btnGeneratePassword.setEnabled(false);
				}
				else {
					isLockRootUserChecked = false;
					textRootPassword.setEnabled(true);
					btnGeneratePassword.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		return composite;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if(!isLockRootUserChecked) {
			if(textRootPassword.getText().equals("")) {
				throw new ValidationException(Messages.getString("FILL_FIELDS"));
			}
			else {
				if(!textRootPassword.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[+=.@*!])(?=\\S+$).{12,}$")) {
					MessageDialog.openWarning(shell, Messages.getString("PASSWORD_VALIDATION_ERROR_HEADER"),Messages.getString("PASSWORD_RULE"));
					throw new ValidationException(Messages.getString("PASSWORD_VALIDATION_ERROR_HEADER"));
				}
			}
		}
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
	Map<String, Object> map = new HashMap<>();
		
		String pass = textRootPassword.getText();
		map.put("RootPassword", pass );
		map.put("lockRootUser", isLockRootUserChecked );
		return map;
	}

	@Override
	public String getCommandId() {
		return "SET_ROOT_PASSWORD";
	}

	@Override
	public String getPluginName() {
		return ManageRootConstant.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ManageRootConstant.PLUGIN_VERSION;
	}
	
	@Override
	public String getMailSubject() {
		
		return "Root Şifresi";
	}

	@Override
	public String getMailContent() {
		
		return "cn={ahenk} ahenkde tanımlamış olduğunuz root şifresi {date} tarihinde başarı ile değiştirilmiştir.";
	}
	
}
