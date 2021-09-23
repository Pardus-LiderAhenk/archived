package tr.org.liderahenk.ldap.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.constants.LdapConstants;
import tr.org.liderahenk.ldap.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * Task execution dialog for ldap plugin.
 * 
 */
public class RenameAgentNameDialog extends DefaultTaskDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(RenameAgentNameDialog.class);
	
	private Text text;
	
	private String dn;
	
	private String  cn="";
	
	/**
	 * @wbp.parser.constructor
	 */
	public RenameAgentNameDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
		this.dn=dn;
		setCnValue();
	}
	
	private void setCnValue() {
		if(dn!=null && !dn.equals("")) {
			String[] dnStr= dn.split(",");
			if(dnStr.length>1) {
				String cnStrValue=dnStr[0];
				if(!"".equals(cnStrValue)) {
					String[] cnStrArr=cnStrValue.split("=");
					
					if(cnStrArr.length>1) {
						cn=cnStrArr[1];
					}
				}				
			}
		}
		
	}

	@Override
	public String createTitle() {
		return Messages.getString("rename_entry");
	}

		
	@Override
	public Control createTaskDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));	
		
		GridData gridData= new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.widthHint = 600;
		gridData.heightHint = 200;
		
		composite.setLayoutData(gridData);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel_1.setText("Lütfen Seçili Kaydın Adını Düzenleyiniz.");
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Seçili Kayıt cn değeri: ");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
		text.setText(cn);
		
		return composite;
	}

	
	@Override
	public void validateBeforeExecution() throws ValidationException {
		if(cn.equals(text.getText()))
			throw new ValidationException("Kayıt üzerinde değişiklik bulunamadı");
	}
	
	
	@Override
	public Map<String, Object> getParameterMap() {
		
		Map<String, Object> map = new HashMap<>();
		
		String newCn=text.getText();
		map.put("dn", dn);
		map.put("oldCn", cn);
		map.put("newCn", newCn);
		return map;
	}

	@Override
	public String getCommandId() {
		return "RENAME_ENTRY";
	}

	@Override
	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	

}
