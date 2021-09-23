package tr.org.liderahenk.firewall.dialogs;

import java.util.HashMap;
import java.util.Map;

import tr.org.liderahenk.firewall.constants.FirewallConstants;
import tr.org.liderahenk.firewall.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class FirewallProfileDialog implements IProfileDialog {
	
	private Text txtRule;
	
	@Override
	public void init() {
	}
	
	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Label lblRule = new Label(composite, SWT.NONE);
		lblRule.setText(Messages.getString("RULE"));
		
		txtRule = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		GridData gridData = new GridData();
		gridData.widthHint = 400;
		gridData.heightHint = 200;
		txtRule.setLayoutData(gridData);
		txtRule.setToolTipText(Messages.getString("SAMPLE_RULE"));
		
		String data = (String) (profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(FirewallConstants.PARAMETERS.RULES) : null);
		
		if(data != null) {
			txtRule.setText(data);
		}
	}
	
	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(FirewallConstants.PARAMETERS.RULES, txtRule.getText());
		return profileData;
	}
	
	@Override
	public void validateBeforeSave() throws ValidationException {
		if(txtRule.getText() == null || txtRule.getText().trim().isEmpty()) {
			throw new ValidationException(Messages.getString("TEXT_AREA_CANNOT_BE_EMPTY"));
		}
	}
	
}
