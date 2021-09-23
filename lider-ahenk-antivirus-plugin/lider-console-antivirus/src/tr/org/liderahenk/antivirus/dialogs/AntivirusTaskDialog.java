package tr.org.liderahenk.antivirus.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.antivirus.constants.AntivirusConstants;
import tr.org.liderahenk.antivirus.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * Task execution dialog for antivirus plugin.
 * 
 */
public class AntivirusTaskDialog extends DefaultTaskDialog {
	
	private Label lblFolderPath;
	private Text txtFolderPath;
	private Label lblDescription;
	
	public AntivirusTaskDialog(Shell parentShell, Set<String> dnSet, boolean activation) {
		super(parentShell, dnSet, activation);
	}


	@Override
	public String createTitle() {
		return "Anlık Tarama Ekranı";
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData  gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		 gd.widthHint = SWT.DEFAULT;
		 gd.heightHint = SWT.DEFAULT;
		composite.setLayoutData( gd);
		


		lblFolderPath = new Label(composite, SWT.NONE);
		lblFolderPath.setText(Messages.getString("FOLDER_PATH"));
		
		txtFolderPath = new Text(composite, SWT.BORDER);
		GridData gd_txtFolderPath = new GridData(SWT.NONE, SWT.LEFT, true, false);
		gd_txtFolderPath.widthHint = 434;
		txtFolderPath.setLayoutData(gd_txtFolderPath);
		txtFolderPath.setFocus();

		final Composite desciptionComposite = new Composite(parent, SWT.NONE);
		desciptionComposite.setLayout(new GridLayout(1, false));
		
		lblDescription = new Label(desciptionComposite, SWT.NONE);
		lblDescription.setText(Messages.getString("FOLDER_PATH_DESCRIPTION"));
		
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if(txtFolderPath == null || txtFolderPath.getText() == null ||  txtFolderPath.getText().isEmpty()){
			throw new ValidationException(Messages.getString("ENTER_FOLDER_PATH"));
		}
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(AntivirusConstants.TASK_PARAMETERS.FOLDER_PATH, txtFolderPath.getText().toString());
		return parameters;
	}

	@Override
	public String getCommandId() {
		return "INSTANT_SCAN";
	}

	@Override
	public String getPluginName() {
		return AntivirusConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return AntivirusConstants.PLUGIN_VERSION;
	}
	
}
