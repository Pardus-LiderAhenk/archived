package tr.org.liderahenk.ldap.dialogs;

import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.constants.LdapConstants;
import tr.org.liderahenk.ldap.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.dialogs.LiderLdapTreeDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * Task execution dialog for ldap plugin.
 * 
 */
public class MoveAgentDialog extends DefaultTaskDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(MoveAgentDialog.class);
	
	private Label lblInfo;
	
	private String dn;
	
	private Combo combo;

	private Button btnParent;

	private Button btnOpenLiderTree;

	private String cn;
	
	public MoveAgentDialog(Shell parentShell, String dn) {
		super(parentShell, dn,true);
		this.dn=dn;
		setCnValue();
	}
	
	@Override
	public void create() {
		super.create();
	}
	

	@Override
	public String createTitle() {
		return Messages.getString("move");
	}

		
	@Override
	public Control createTaskDialogArea(Composite parent) {
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
	public void validateBeforeExecution() throws ValidationException {
		if(dn.equals(combo.getText()))
			throw new ValidationException("Kayıt üzerinde değişiklik bulunamadı");
		if("".equals(combo.getText()))
			throw new ValidationException("Lütfen üst öğe seçiniz.");
		if(cn == null || "".equals(cn)) {
			throw new ValidationException("İşlem Yapılırken Hata Oluştu.");
		}
	}
	
	
	@Override
	public Map<String, Object> getParameterMap() {
		
		Map<String, Object> map = new HashMap<>();
		
		String newParentDn=combo.getText();
		
		
		map.put("dn", dn);
		map.put("newParentDn", newParentDn);
		return map;
	}

	@Override
	public String getCommandId() {
		return "MOVE_AGENT";
	}

	@Override
	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	
	private void setCnValue() {
		cn="";
		if(dn!=null && !dn.equals("")) {
			String[] dnStr= dn.split(",");
			if(dnStr.length>1) {
				cn=dnStr[0]+",";
			}
		}
		
	}
}
